package com.example.launcher

import android.app.ActivityOptions
import android.appwidget.AppWidgetProviderInfo
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.ui.geometry.Rect
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.animations.AppTransitionPhase
import com.example.database.HomeItemEntity
import com.example.database.LauncherLayoutRepository
import com.example.database.LauncherSettings
import com.example.database.SettingsRepository
import com.example.database.WallpaperPreset
import com.example.notifications.NotificationBadgeRepository
import com.example.widgets.AppWidgetHostManager
import com.example.widgets.CalendarReader
import com.example.widgets.CalendarWidgetData
import com.example.widgets.CitySearchResult
import com.example.widgets.IosWidgetKind
import com.example.widgets.WeatherRepository
import com.example.widgets.WeatherUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LauncherViewModel(
    private val appContext: Context,
    private val layoutRepository: LauncherLayoutRepository,
    private val settingsRepository: SettingsRepository,
    val appCatalogManager: AppCatalogManager,
    val widgetHostManager: AppWidgetHostManager,
    val weatherRepository: WeatherRepository
) : ViewModel() {

    val settings: StateFlow<LauncherSettings> = settingsRepository.settingsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LauncherSettings())

    val installedApps: StateFlow<List<InstalledAppInfo>> = appCatalogManager.installedApps

    val homeItems: StateFlow<List<HomeItemEntity>> = layoutRepository.homeItemsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val badgePrefsMap: StateFlow<Map<String, Boolean>> = layoutRepository.badgePrefsFlow
        .combine(NotificationBadgeRepository.counts) { prefs, _ ->
            prefs.associate { it.packageName to it.enabled }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    val filteredBadgeCounts: StateFlow<Map<String, Int>> = combine(
        NotificationBadgeRepository.counts,
        layoutRepository.badgePrefsFlow,
        settings
    ) { rawCounts, prefs, currentSettings ->
        if (!currentSettings.badgesEnabled) {
            emptyMap()
        } else {
            val disabledSet = prefs.filter { !it.enabled }.map { it.packageName }.toSet()
            rawCounts.filterKeys { it !in disabledSet }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    val recentSearches: StateFlow<List<String>> = layoutRepository.recentSearchesFlow
        .combine(settings) { recents, s ->
            if (s.searchShowRecents) recents.map { it.query } else emptyList()
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val weatherState: StateFlow<WeatherUiState> = weatherRepository.state

    private val _calendarData = MutableStateFlow<CalendarWidgetData>(CalendarWidgetData.NeedsPermission)
    val calendarData: StateFlow<CalendarWidgetData> = _calendarData.asStateFlow()

    private val _isEditMode = MutableStateFlow(false)
    val isEditMode: StateFlow<Boolean> = _isEditMode.asStateFlow()

    private val _transitionPhase = MutableStateFlow<AppTransitionPhase>(AppTransitionPhase.Idle)
    val transitionPhase: StateFlow<AppTransitionPhase> = _transitionPhase.asStateFlow()

    private val _pendingInternalRoute = MutableStateFlow<String?>(null)
    val pendingInternalRoute: StateFlow<String?> = _pendingInternalRoute.asStateFlow()

    private val _lastLaunchedExternalItem = MutableStateFlow<HomeItemEntity?>(null)
    private val iconBoundsMap = mutableMapOf<String, Rect>()

    init {
        viewModelScope.launch {
            combine(appCatalogManager.installedApps, settingsRepository.settingsFlow) { apps, s ->
                apps to s
            }.collect { (apps, s) ->
                layoutRepository.ensureInitializedAndSynced(
                    installedApps = apps,
                    gridCols = s.gridColumns,
                    gridRows = 6
                )
            }
        }

        viewModelScope.launch {
            val s = settingsRepository.settingsFlow.first()
            if (s.weatherCity.isNotBlank() || (s.weatherLat != null && s.weatherLon != null)) {
                weatherRepository.refreshWeather(s.weatherCity, s.weatherLat, s.weatherLon)
            }
        }

        refreshCalendar()
    }

    fun registerIconBounds(itemId: String, bounds: Rect) {
        iconBoundsMap[itemId] = bounds
    }

    fun getIconBounds(itemId: String): Rect? = iconBoundsMap[itemId]

    fun setEditMode(enabled: Boolean) {
        _isEditMode.value = enabled
    }

    fun refreshCalendar() {
        viewModelScope.launch {
            _calendarData.value = CalendarReader.readTodayEvents(appContext)
        }
    }

    fun startAppOpenTransition(item: HomeItemEntity, fallbackCenterBounds: Rect) {
        if (_isEditMode.value) return
        val sourceBounds = iconBoundsMap[item.id] ?: fallbackCenterBounds
        val isInternal = item.packageName == AppCatalogManager.INTERNAL_LAUNCHER_SETTINGS_PKG ||
            item.packageName == AppCatalogManager.INTERNAL_WEATHER_PKG
        _transitionPhase.value = AppTransitionPhase.Opening(
            item = item,
            sourceBounds = sourceBounds,
            isInternalTarget = isInternal
        )
    }

    fun onTransitionReadyToLaunch(
        item: HomeItemEntity,
        onOpenInternalRoute: (String) -> Unit
    ) {
        when (item.packageName) {
            AppCatalogManager.INTERNAL_LAUNCHER_SETTINGS_PKG -> {
                _pendingInternalRoute.value = item.id
                onOpenInternalRoute("settings")
            }
            AppCatalogManager.INTERNAL_WEATHER_PKG -> {
                _pendingInternalRoute.value = item.id
                onOpenInternalRoute("weather_config")
            }
            else -> {
                _lastLaunchedExternalItem.value = item
                launchExternalApp(item)
            }
        }
    }

    fun triggerClosingAnimationIfNeeded() {
        val lastItem = _lastLaunchedExternalItem.value ?: return
        _lastLaunchedExternalItem.value = null
        val targetBounds = iconBoundsMap[lastItem.id] ?: return
        _transitionPhase.value = AppTransitionPhase.Closing(
            item = lastItem,
            targetBounds = targetBounds
        )
    }

    fun triggerInternalClosingAnimation(itemId: String?) {
        if (itemId == null) return
        val item = homeItems.value.find { it.id == itemId } ?: return
        val targetBounds = iconBoundsMap[itemId] ?: return
        _transitionPhase.value = AppTransitionPhase.Closing(
            item = item,
            targetBounds = targetBounds
        )
    }

    fun clearPendingInternalRoute(): String? {
        val id = _pendingInternalRoute.value
        _pendingInternalRoute.value = null
        return id
    }

    fun onTransitionFinished() {
        _transitionPhase.value = AppTransitionPhase.Idle
    }

    fun launchExternalApp(item: HomeItemEntity) {
        val bounds = iconBoundsMap[item.id]
        val pm = appContext.packageManager
        val intent = if (item.activityName.isNotBlank()) {
            Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
                component = ComponentName(item.packageName, item.activityName)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
            }
        } else {
            pm.getLaunchIntentForPackage(item.packageName)?.apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }

        if (intent == null) {
            Toast.makeText(appContext, "${item.label} cannot be launched", Toast.LENGTH_SHORT).show()
            return
        }

        val bundle = if (bounds != null && settings.value.animationsEnabled && settings.value.appOpenAnimationEnabled) {
            ActivityOptions.makeScaleUpAnimation(
                android.view.View(appContext),
                bounds.left.toInt(),
                bounds.top.toInt(),
                bounds.width.toInt().coerceAtLeast(1),
                bounds.height.toInt().coerceAtLeast(1)
            ).toBundle()
        } else null

        runCatching {
            if (bundle != null) {
                appContext.startActivity(intent, bundle)
            } else {
                appContext.startActivity(intent)
            }
        }.onFailure {
            Toast.makeText(appContext, "Unable to open ${item.label}", Toast.LENGTH_SHORT).show()
        }
    }

    fun launchInstalledApp(app: InstalledAppInfo) {
        val fakeEntity = HomeItemEntity(
            id = "search_${app.packageName}",
            itemType = "APP",
            packageName = app.packageName,
            activityName = app.activityName,
            label = app.label,
            pageIndex = 0,
            gridRow = 0,
            gridCol = 0
        )
        launchExternalApp(fakeEntity)
    }

    fun moveOrSwapItem(draggedId: String, targetPage: Int, targetRow: Int, targetCol: Int) {
        if (settings.value.lockLayout) return
        viewModelScope.launch {
            layoutRepository.moveOrSwapItem(
                draggedId = draggedId,
                targetPage = targetPage,
                targetRow = targetRow,
                targetCol = targetCol,
                gridCols = settings.value.gridColumns
            )
        }
    }

    fun removeItemFromHome(item: HomeItemEntity) {
        viewModelScope.launch {
            if (item.itemType == "WIDGET" && item.appWidgetId != -1) {
                widgetHostManager.deleteAppWidgetId(item.appWidgetId)
            }
            layoutRepository.removeItemFromHome(item.id)
        }
    }

    fun addAppBackToHome(app: InstalledAppInfo) {
        viewModelScope.launch {
            layoutRepository.addAppToHome(app, gridCols = settings.value.gridColumns)
        }
    }

    fun addIosWidget(kind: IosWidgetKind, spanCols: Int, spanRows: Int) {
        viewModelScope.launch {
            layoutRepository.addWidgetToHome(
                kind = kind,
                spanCols = spanCols,
                spanRows = spanRows,
                gridCols = settings.value.gridColumns
            )
        }
    }

    fun tryAddAndroidWidget(
        provider: AppWidgetProviderInfo,
        spanCols: Int,
        spanRows: Int,
        onNeedBindPermission: (appWidgetId: Int, provider: AppWidgetProviderInfo, spanCols: Int, spanRows: Int) -> Unit
    ) {
        val id = widgetHostManager.allocateWidgetId()
        val bound = widgetHostManager.bindAppWidgetIdIfAllowed(id, provider)
        if (bound) {
            finalizeBoundAndroidWidget(id, provider, spanCols, spanRows)
        } else {
            onNeedBindPermission(id, provider, spanCols, spanRows)
        }
    }

    fun finalizeBoundAndroidWidget(
        appWidgetId: Int,
        provider: AppWidgetProviderInfo,
        spanCols: Int,
        spanRows: Int
    ) {
        val label = provider.loadLabel(appContext.packageManager)
        viewModelScope.launch {
            layoutRepository.addWidgetToHome(
                kind = IosWidgetKind.ANDROID_HOST,
                spanCols = spanCols,
                spanRows = spanRows,
                appWidgetId = appWidgetId,
                label = label,
                packageName = provider.provider.packageName,
                gridCols = settings.value.gridColumns
            )
        }
    }

    fun updateWidgetNote(id: String, note: String) {
        viewModelScope.launch { layoutRepository.updateWidgetNote(id, note) }
    }

    fun updateSettings(transform: (LauncherSettings) -> LauncherSettings) {
        viewModelScope.launch { settingsRepository.update(transform) }
    }

    fun applyWallpaper(
        preset: WallpaperPreset,
        customPath: String,
        scale: Float,
        offsetX: Float,
        offsetY: Float
    ) {
        updateSettings {
            it.copy(
                wallpaperPreset = preset,
                customWallpaperPath = customPath,
                wallpaperScale = scale,
                wallpaperOffsetX = offsetX,
                wallpaperOffsetY = offsetY
            )
        }
    }

    fun setWeatherCity(city: CitySearchResult) {
        viewModelScope.launch {
            settingsRepository.update {
                it.copy(
                    weatherCity = city.name,
                    weatherLat = city.lat,
                    weatherLon = city.lon
                )
            }
            weatherRepository.refreshWeather(city.name, city.lat, city.lon, isLocationBased = false)
        }
    }

    fun useDeviceLocationForWeather() {
        viewModelScope.launch {
            val resolved = weatherRepository.resolveDeviceCoarseLocation()
            if (resolved == null) {
                Toast.makeText(appContext, "Location unavailable — enter city manually", Toast.LENGTH_SHORT).show()
                return@launch
            }
            val (city, lat, lon) = resolved
            settingsRepository.update {
                it.copy(weatherCity = city, weatherLat = lat, weatherLon = lon)
            }
            weatherRepository.refreshWeather(city, lat, lon, isLocationBased = true)
        }
    }

    fun setBadgeEnabledForPackage(packageName: String, enabled: Boolean) {
        viewModelScope.launch { layoutRepository.setBadgeEnabledForPackage(packageName, enabled) }
    }

    fun recordRecentSearch(query: String) {
        viewModelScope.launch { layoutRepository.addRecentSearch(query) }
    }

    fun clearRecentSearches() {
        viewModelScope.launch { layoutRepository.clearRecentSearches() }
    }

    fun resetHomeLayout() {
        viewModelScope.launch {
            layoutRepository.resetLayout(installedApps.value, settings.value.gridColumns)
        }
    }

    suspend fun exportBackup(): String = layoutRepository.exportBackupJson()

    suspend fun importBackup(json: String): Boolean = layoutRepository.importBackupJson(json)

    class Factory(
        private val appContext: Context,
        private val layoutRepository: LauncherLayoutRepository,
        private val settingsRepository: SettingsRepository,
        private val appCatalogManager: AppCatalogManager,
        private val widgetHostManager: AppWidgetHostManager,
        private val weatherRepository: WeatherRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return LauncherViewModel(
                appContext,
                layoutRepository,
                settingsRepository,
                appCatalogManager,
                widgetHostManager,
                weatherRepository
            ) as T
        }
    }
}
