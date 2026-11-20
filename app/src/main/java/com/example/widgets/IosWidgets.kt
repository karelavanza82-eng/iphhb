package com.example.widgets

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.net.Uri
import android.os.BatteryManager
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.BatteryStd
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.example.database.HomeItemEntity
import com.example.ui.theme.IosBlue
import com.example.ui.theme.IosGreen
import com.example.ui.theme.IosOrange
import com.example.ui.theme.IosRed
import com.example.ui.theme.IosSansFontFamily
import kotlinx.coroutines.delay
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

enum class IosWidgetKind(val title: String, val defaultSpanCols: Int, val defaultSpanRows: Int) {
    WEATHER("Weather", 2, 2),
    CALENDAR("Calendar", 2, 2),
    CLOCK("Clock", 2, 2),
    BATTERY("Battery", 2, 2),
    PHOTO("Photos", 2, 2),
    MUSIC("Music", 4, 2),
    CUSTOM("Notes", 2, 2),
    ANDROID_HOST("System Widget", 4, 2)
}

@Composable
fun IosWidgetCard(
    item: HomeItemEntity,
    weatherState: WeatherUiState,
    useCelsius: Boolean,
    calendarData: CalendarWidgetData,
    photoWidgetUri: String,
    hostManager: AppWidgetHostManager,
    widgetWidthDp: Int,
    widgetHeightDp: Int,
    onWidgetClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(22.dp)
    val kind = remember(item.widgetKind) {
        runCatching { IosWidgetKind.valueOf(item.widgetKind) }.getOrDefault(IosWidgetKind.CUSTOM)
    }

    Box(
        modifier = modifier
            .clip(shape)
            .clickable(onClick = onWidgetClick)
    ) {
        when (kind) {
            IosWidgetKind.WEATHER -> WeatherWidgetContent(weatherState, useCelsius)
            IosWidgetKind.CALENDAR -> CalendarWidgetContent(calendarData)
            IosWidgetKind.CLOCK -> ClockWidgetContent()
            IosWidgetKind.BATTERY -> BatteryWidgetContent()
            IosWidgetKind.PHOTO -> PhotoWidgetContent(photoWidgetUri)
            IosWidgetKind.MUSIC -> MusicWidgetContent()
            IosWidgetKind.CUSTOM -> CustomNotesWidgetContent(item.label)
            IosWidgetKind.ANDROID_HOST -> AndroidAppWidgetView(
                hostManager = hostManager,
                appWidgetId = item.appWidgetId,
                widthDp = widgetWidthDp,
                heightDp = widgetHeightDp
            )
        }
    }
}

@Composable
private fun WeatherWidgetContent(
    state: WeatherUiState,
    useCelsius: Boolean
) {
    val bgBrush = when (state) {
        is WeatherUiState.Loaded -> {
            if (state.isDay) {
                Brush.verticalGradient(listOf(Color(0xFF2D659E), Color(0xFF477DB2)))
            } else {
                Brush.verticalGradient(listOf(Color(0xFF162544), Color(0xFF28436E)))
            }
        }
        else -> Brush.verticalGradient(listOf(Color(0xFF2E639B), Color(0xFF487EB2)))
    }

    fun formatTemp(c: Double): String {
        val value = if (useCelsius) c else (c * 9.0 / 5.0 + 32.0)
        return "${value.roundToInt()}°"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgBrush)
            .padding(14.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        when (state) {
            is WeatherUiState.Loaded -> {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = state.city,
                            color = Color.White,
                            fontFamily = IosSansFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        if (state.isLocationBased) {
                            Spacer(Modifier.width(3.dp))
                            Icon(
                                imageVector = Icons.Default.NearMe,
                                contentDescription = "Current location",
                                tint = Color.White,
                                modifier = Modifier.size(11.dp)
                            )
                        }
                    }
                    Text(
                        text = formatTemp(state.tempC),
                        color = Color.White,
                        fontFamily = IosSansFontFamily,
                        fontWeight = FontWeight.Light,
                        fontSize = 42.sp,
                        lineHeight = 46.sp
                    )
                }

                Column {
                    val icon = when (state.wmoCode) {
                        0, 1 -> Icons.Default.WbSunny
                        95, 96, 99 -> Icons.Default.Thunderstorm
                        else -> Icons.Default.Cloud
                    }
                    Icon(
                        imageVector = icon,
                        contentDescription = state.condition,
                        tint = if (state.wmoCode in 0..1) Color(0xFFFFD60A) else Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = state.condition,
                        color = Color.White,
                        fontFamily = IosSansFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        maxLines = 1
                    )
                    Text(
                        text = "H:${formatTemp(state.highC)} L:${formatTemp(state.lowC)}",
                        color = Color.White.copy(alpha = 0.9f),
                        fontFamily = IosSansFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp
                    )
                }
            }

            is WeatherUiState.Loading -> {
                Column {
                    Text(
                        text = state.city,
                        color = Color.White,
                        fontFamily = IosSansFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "--°",
                        color = Color.White,
                        fontFamily = IosSansFontFamily,
                        fontWeight = FontWeight.Light,
                        fontSize = 40.sp
                    )
                }
                Text(
                    text = "Updating...",
                    color = Color.White.copy(alpha = 0.85f),
                    fontFamily = IosSansFontFamily,
                    fontSize = 12.sp
                )
            }

            is WeatherUiState.Error -> {
                Column {
                    Text(
                        text = state.city.ifBlank { "Weather" },
                        color = Color.White,
                        fontFamily = IosSansFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        maxLines = 1
                    )
                    Text(
                        text = "--°",
                        color = Color.White,
                        fontFamily = IosSansFontFamily,
                        fontWeight = FontWeight.Light,
                        fontSize = 40.sp
                    )
                }
                Column {
                    Text(
                        text = state.message,
                        color = Color.White.copy(alpha = 0.9f),
                        fontFamily = IosSansFontFamily,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "Tap to retry",
                        color = Color.White.copy(alpha = 0.72f),
                        fontFamily = IosSansFontFamily,
                        fontSize = 11.sp
                    )
                }
            }

            WeatherUiState.Unconfigured -> {
                Column {
                    Text(
                        text = "Weather",
                        color = Color.White,
                        fontFamily = IosSansFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "--°",
                        color = Color.White,
                        fontFamily = IosSansFontFamily,
                        fontWeight = FontWeight.Light,
                        fontSize = 40.sp
                    )
                }
                Column {
                    Icon(
                        imageVector = Icons.Default.Cloud,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = "Tap to set city",
                        color = Color.White.copy(alpha = 0.9f),
                        fontFamily = IosSansFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun CalendarWidgetContent(data: CalendarWidgetData) {
    val now = remember { Date() }
    val weekday = remember(now) {
        SimpleDateFormat("EEEE", Locale.ENGLISH).format(now).uppercase(Locale.ENGLISH)
    }
    val day = remember(now) {
        SimpleDateFormat("d", Locale.ENGLISH).format(now)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(14.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = weekday,
                color = IosRed,
                fontFamily = IosSansFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
                letterSpacing = 0.4.sp
            )
            Text(
                text = day,
                color = Color(0xFF1C1C1E),
                fontFamily = IosSansFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 32.sp,
                lineHeight = 35.sp
            )
        }

        when (data) {
            CalendarWidgetData.NeedsPermission -> {
                Text(
                    text = "Tap to show calendar events",
                    color = Color(0xFF8E8E93),
                    fontFamily = IosSansFontFamily,
                    fontSize = 12.sp,
                    lineHeight = 15.sp
                )
            }
            is CalendarWidgetData.Loaded -> {
                if (data.events.isEmpty()) {
                    Text(
                        text = "No events today",
                        color = Color(0xFF8E8E93),
                        fontFamily = IosSansFontFamily,
                        fontSize = 13.sp
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        data.events.take(2).forEach { ev ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .width(3.dp)
                                        .height(14.dp)
                                        .clip(CircleShape)
                                        .background(IosBlue)
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = "${ev.timeLabel} ${ev.title}",
                                    color = Color(0xFF1C1C1E),
                                    fontFamily = IosSansFontFamily,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 11.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ClockWidgetContent() {
    var nowMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            nowMillis = System.currentTimeMillis()
            delay(1000L)
        }
    }
    val cal = remember(nowMillis) { Calendar.getInstance().apply { timeInMillis = nowMillis } }
    val sec = cal.get(Calendar.SECOND)
    val min = cal.get(Calendar.MINUTE)
    val hour = cal.get(Calendar.HOUR)
    val dateLabel = remember(nowMillis / 60000L) {
        SimpleDateFormat("d MMM", Locale.ENGLISH).format(Date(nowMillis))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F7))
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val radius = size.minDimension / 2f * 0.92f
            val center = Offset(size.width / 2f, size.height / 2f)
            drawCircle(Color.White, radius = radius, center = center)
            for (i in 0 until 60) {
                val angle = i * 6.0 * PI / 180.0
                val isHour = i % 5 == 0
                val inner = if (isHour) radius * 0.80f else radius * 0.88f
                val outer = radius * 0.94f
                drawLine(
                    color = if (isHour) Color(0xFF1C1C1E) else Color(0xFFC7C7CC),
                    start = Offset(center.x + (cos(angle) * inner).toFloat(), center.y + (sin(angle) * inner).toFloat()),
                    end = Offset(center.x + (cos(angle) * outer).toFloat(), center.y + (sin(angle) * outer).toFloat()),
                    strokeWidth = if (isHour) 2.5.dp.toPx() else 1.2.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
            val hourAngle = ((hour + min / 60f) * 30f - 90f) * PI / 180.0
            drawLine(
                color = Color(0xFF1C1C1E),
                start = center,
                end = Offset(center.x + (cos(hourAngle) * radius * 0.48f).toFloat(), center.y + (sin(hourAngle) * radius * 0.48f).toFloat()),
                strokeWidth = 3.5.dp.toPx(),
                cap = StrokeCap.Round
            )
            val minAngle = ((min + sec / 60f) * 6f - 90f) * PI / 180.0
            drawLine(
                color = Color(0xFF1C1C1E),
                start = center,
                end = Offset(center.x + (cos(minAngle) * radius * 0.72f).toFloat(), center.y + (sin(minAngle) * radius * 0.72f).toFloat()),
                strokeWidth = 2.4.dp.toPx(),
                cap = StrokeCap.Round
            )
            val secAngle = (sec * 6f - 90f) * PI / 180.0
            drawLine(
                color = IosOrange,
                start = center,
                end = Offset(center.x + (cos(secAngle) * radius * 0.80f).toFloat(), center.y + (sin(secAngle) * radius * 0.80f).toFloat()),
                strokeWidth = 1.4.dp.toPx(),
                cap = StrokeCap.Round
            )
            drawCircle(IosOrange, radius = 3.5.dp.toPx(), center = center)
        }
        Text(
            text = dateLabel,
            color = Color(0xFF8E8E93),
            fontFamily = IosSansFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 10.sp,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 26.dp)
        )
    }
}

@Composable
private fun BatteryWidgetContent() {
    val context = LocalContext.current
    var batteryPct by remember { mutableIntStateOf(100) }
    var isCharging by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (true) {
            val status = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            if (status != null) {
                val level = status.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale = status.getIntExtra(BatteryManager.EXTRA_SCALE, 100)
                if (level >= 0 && scale > 0) {
                    batteryPct = (level * 100) / scale
                }
                val plugged = status.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0)
                isCharging = plugged != 0
            }
            delay(15_000L)
        }
    }

    val ringColor = when {
        isCharging || batteryPct > 25 -> IosGreen
        else -> IosRed
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1C1C1E))
            .padding(14.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Box(modifier = Modifier.size(54.dp), contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawArc(
                    color = Color.White.copy(alpha = 0.16f),
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(width = 6.dp.toPx())
                )
                drawArc(
                    color = ringColor,
                    startAngle = -90f,
                    sweepAngle = 360f * (batteryPct / 100f),
                    useCenter = false,
                    style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                )
            }
            Icon(
                imageVector = if (isCharging) Icons.Default.BatteryChargingFull else Icons.Default.BatteryStd,
                contentDescription = "Battery status",
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }

        Column {
            Text(
                text = "$batteryPct%",
                color = Color.White,
                fontFamily = IosSansFontFamily,
                fontWeight = FontWeight.Light,
                fontSize = 26.sp
            )
            Text(
                text = if (isCharging) "Charging" else "Device Battery",
                color = Color.White.copy(alpha = 0.72f),
                fontFamily = IosSansFontFamily,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun PhotoWidgetContent(photoUri: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2C2C2E)),
        contentAlignment = Alignment.Center
    ) {
        if (photoUri.isNotBlank()) {
            val model: Any = if (photoUri.startsWith("/")) File(photoUri) else Uri.parse(photoUri)
            AsyncImage(
                model = model,
                contentDescription = "Photo widget",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Photo,
                    contentDescription = "Photos",
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(30.dp)
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Tap to pick photo",
                    color = Color.White.copy(alpha = 0.85f),
                    fontFamily = IosSansFontFamily,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun MusicWidgetContent() {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFFFA2D48), Color(0xFFB31B34))
                )
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color.White.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.MusicNote,
                contentDescription = "Music",
                tint = Color.White,
                modifier = Modifier.size(34.dp)
            )
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Apple Music",
                color = Color.White.copy(alpha = 0.82f),
                fontFamily = IosSansFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "Tap to open Music",
                color = Color.White,
                fontFamily = IosSansFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
            Text(
                text = "Quick launch player",
                color = Color.White.copy(alpha = 0.78f),
                fontFamily = IosSansFontFamily,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun CustomNotesWidgetContent(noteText: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFBE6))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(Color(0xFFFFD60A), Color(0xFFFFC300))))
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Quick Note",
                color = Color(0xFF4A3B00),
                fontFamily = IosSansFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
            )
        }
        Text(
            text = noteText.ifBlank { "Tap to edit quick note" },
            color = Color(0xFF2C2C2E),
            fontFamily = IosSansFontFamily,
            fontSize = 13.sp,
            modifier = Modifier.padding(12.dp),
            maxLines = 4,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun AndroidAppWidgetView(
    hostManager: AppWidgetHostManager,
    appWidgetId: Int,
    widthDp: Int,
    heightDp: Int
) {
    val hostView = remember(appWidgetId, widthDp, heightDp) {
        hostManager.createHostView(appWidgetId, widthDp, heightDp)
    }
    if (hostView != null) {
        AndroidView(
            factory = { hostView },
            modifier = Modifier.fillMaxSize()
        )
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF2C2C2E)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Widget unavailable",
                color = Color.White.copy(alpha = 0.75f),
                fontFamily = IosSansFontFamily,
                fontSize = 12.sp
            )
        }
    }
}
