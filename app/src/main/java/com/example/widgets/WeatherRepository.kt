package com.example.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.LocationManager
import com.example.permissions.PermissionsManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.net.URLEncoder
import java.util.Locale
import java.util.concurrent.TimeUnit

sealed interface WeatherUiState {
    data object Unconfigured : WeatherUiState
    data class Loading(val city: String) : WeatherUiState
    data class Loaded(
        val city: String,
        val tempC: Double,
        val highC: Double,
        val lowC: Double,
        val condition: String,
        val wmoCode: Int,
        val isDay: Boolean,
        val isLocationBased: Boolean
    ) : WeatherUiState
    data class Error(val city: String, val message: String) : WeatherUiState
}

data class CitySearchResult(
    val name: String,
    val admin: String,
    val country: String,
    val lat: Double,
    val lon: Double
)

class WeatherRepository(private val context: Context) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    private val _state = MutableStateFlow<WeatherUiState>(WeatherUiState.Unconfigured)
    val state: StateFlow<WeatherUiState> = _state.asStateFlow()

    suspend fun refreshWeather(
        city: String,
        lat: Double?,
        lon: Double?,
        isLocationBased: Boolean = false
    ) = withContext(Dispatchers.IO) {
        if (lat == null || lon == null) {
            if (city.isBlank()) {
                _state.value = WeatherUiState.Unconfigured
                return@withContext
            }
            val found = searchCities(city).firstOrNull()
            if (found == null) {
                _state.value = WeatherUiState.Error(city, "Location unavailable")
                return@withContext
            }
            fetchForecast(found.name, found.lat, found.lon, isLocationBased)
        } else {
            fetchForecast(city.ifBlank { "Current Location" }, lat, lon, isLocationBased)
        }
    }

    @SuppressLint("MissingPermission")
    suspend fun resolveDeviceCoarseLocation(): Triple<String, Double, Double>? =
        withContext(Dispatchers.IO) {
            if (!PermissionsManager.hasCoarseLocationPermission(context)) return@withContext null
            val lm = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager ?: return@withContext null
            val providers = lm.getProviders(true)
            val bestLoc = providers.mapNotNull { provider ->
                runCatching { lm.getLastKnownLocation(provider) }.getOrNull()
            }.maxByOrNull { it.time } ?: return@withContext null

            val cityName = runCatching {
                @Suppress("DEPRECATION")
                Geocoder(context, Locale.getDefault())
                    .getFromLocation(bestLoc.latitude, bestLoc.longitude, 1)
                    ?.firstOrNull()
                    ?.let { it.subLocality ?: it.locality ?: it.subAdminArea ?: it.adminArea }
            }.getOrNull() ?: "Current Location"

            Triple(cityName, bestLoc.latitude, bestLoc.longitude)
        }

    suspend fun searchCities(query: String): List<CitySearchResult> = withContext(Dispatchers.IO) {
        val trimmed = query.trim()
        if (trimmed.length < 2) return@withContext emptyList()
        val encoded = URLEncoder.encode(trimmed, "UTF-8")
        val url = "https://geocoding-api.open-meteo.com/v1/search?name=$encoded&count=6&language=en&format=json"
        runCatching {
            val req = Request.Builder().url(url).build()
            client.newCall(req).execute().use { resp ->
                if (!resp.isSuccessful) return@use emptyList()
                val body = resp.body?.string() ?: return@use emptyList()
                val json = JSONObject(body)
                val results = json.optJSONArray("results") ?: return@use emptyList()
                buildList {
                    for (i in 0 until results.length()) {
                        val obj = results.getJSONObject(i)
                        add(
                            CitySearchResult(
                                name = obj.optString("name"),
                                admin = obj.optString("admin1"),
                                country = obj.optString("country"),
                                lat = obj.optDouble("latitude"),
                                lon = obj.optDouble("longitude")
                            )
                        )
                    }
                }
            }
        }.getOrDefault(emptyList())
    }

    private suspend fun fetchForecast(
        city: String,
        lat: Double,
        lon: Double,
        isLocationBased: Boolean
    ) {
        _state.value = WeatherUiState.Loading(city)
        val url = "https://api.open-meteo.com/v1/forecast?latitude=$lat&longitude=$lon" +
            "&current=temperature_2m,weather_code,is_day" +
            "&daily=temperature_2m_max,temperature_2m_min&timezone=auto&forecast_days=1"
        runCatching {
            val req = Request.Builder().url(url).build()
            client.newCall(req).execute().use { resp ->
                if (!resp.isSuccessful) {
                    _state.value = WeatherUiState.Error(city, "Weather service offline")
                    return
                }
                val body = resp.body?.string() ?: run {
                    _state.value = WeatherUiState.Error(city, "Empty weather response")
                    return
                }
                val json = JSONObject(body)
                val current = json.getJSONObject("current")
                val daily = json.getJSONObject("daily")
                val temp = current.getDouble("temperature_2m")
                val code = current.getInt("weather_code")
                val isDay = current.optInt("is_day", 1) == 1
                val high = daily.getJSONArray("temperature_2m_max").getDouble(0)
                val low = daily.getJSONArray("temperature_2m_min").getDouble(0)
                _state.value = WeatherUiState.Loaded(
                    city = city,
                    tempC = temp,
                    highC = high,
                    lowC = low,
                    condition = wmoCodeToCondition(code),
                    wmoCode = code,
                    isDay = isDay,
                    isLocationBased = isLocationBased
                )
            }
        }.onFailure {
            _state.value = WeatherUiState.Error(city, "Offline")
        }
    }

    private fun wmoCodeToCondition(code: Int): String = when (code) {
        0 -> "Clear Sky"
        1 -> "Mainly Clear"
        2 -> "Partly Cloudy"
        3 -> "Mostly Cloudy"
        45, 48 -> "Foggy"
        51, 53, 55 -> "Drizzle"
        61, 63, 65 -> "Rain"
        71, 73, 75 -> "Snow"
        80, 81, 82 -> "Showers"
        95, 96, 99 -> "Thunderstorm"
        else -> "Cloudy"
    }
}
