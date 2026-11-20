package com.example.icons

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.IosSansFontFamily
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun DrawIosIconCatalogA(
    key: IosIconKey,
    iconSize: Dp,
    darkThemeMode: Boolean,
    modifier: Modifier = Modifier
) {
    when (key) {
        IosIconKey.CALENDAR -> IosCalendarArtwork(iconSize, darkThemeMode, modifier)
        IosIconKey.CLOCK -> IosClockArtwork(iconSize, modifier)
        else -> Canvas(modifier = modifier.fillMaxSize()) {
            when (key) {
                IosIconKey.PHONE -> drawPhoneArtwork()
                IosIconKey.MESSAGES -> drawMessagesArtwork()
                IosIconKey.SAFARI -> drawSafariArtwork(darkThemeMode)
                IosIconKey.MUSIC -> drawMusicArtwork()
                IosIconKey.PHOTOS -> drawPhotosArtwork(darkThemeMode)
                IosIconKey.CAMERA -> drawCameraArtwork()
                IosIconKey.SETTINGS -> drawSettingsArtwork()
                IosIconKey.WEATHER -> drawWeatherArtwork()
                IosIconKey.MAPS -> drawMapsArtwork()
                IosIconKey.APP_STORE -> drawAppStoreArtwork()
                IosIconKey.FACETIME -> drawFaceTimeArtwork()
                IosIconKey.PODCASTS -> drawPodcastsArtwork()
                IosIconKey.HEALTH -> drawHealthArtwork(darkThemeMode)
                else -> Unit
            }
        }
    }
}

private fun DrawScope.drawPhoneArtwork() {
    drawRect(
        brush = Brush.verticalGradient(listOf(Color(0xFF67F07A), Color(0xFF28CD41)))
    )
    val w = size.width
    val h = size.height
    val path = Path().apply {
        moveTo(w * 0.30f, h * 0.24f)
        cubicTo(w * 0.35f, h * 0.20f, w * 0.42f, h * 0.27f, w * 0.44f, h * 0.33f)
        cubicTo(w * 0.46f, h * 0.38f, w * 0.40f, h * 0.42f, w * 0.42f, h * 0.46f)
        cubicTo(w * 0.45f, h * 0.52f, w * 0.49f, h * 0.56f, w * 0.55f, h * 0.59f)
        cubicTo(w * 0.59f, h * 0.61f, w * 0.63f, h * 0.55f, w * 0.68f, h * 0.57f)
        cubicTo(w * 0.74f, h * 0.59f, w * 0.81f, h * 0.66f, w * 0.77f, h * 0.71f)
        cubicTo(w * 0.72f, h * 0.78f, w * 0.60f, h * 0.78f, w * 0.49f, h * 0.71f)
        cubicTo(w * 0.37f, h * 0.64f, w * 0.28f, h * 0.54f, w * 0.24f, h * 0.43f)
        cubicTo(w * 0.20f, h * 0.33f, w * 0.24f, h * 0.27f, w * 0.30f, h * 0.24f)
        close()
    }
    drawPath(path, color = Color.White)
}

private fun DrawScope.drawMessagesArtwork() {
    drawRect(
        brush = Brush.verticalGradient(listOf(Color(0xFF6AF27C), Color(0xFF23C93E)))
    )
    val w = size.width
    val h = size.height
    drawOval(
        color = Color.White,
        topLeft = Offset(w * 0.17f, h * 0.22f),
        size = Size(w * 0.66f, h * 0.51f)
    )
    val tail = Path().apply {
        moveTo(w * 0.28f, h * 0.63f)
        lineTo(w * 0.19f, h * 0.76f)
        lineTo(w * 0.38f, h * 0.70f)
        close()
    }
    drawPath(tail, color = Color.White)
}

private fun DrawScope.drawSafariArtwork(darkThemeMode: Boolean) {
    drawRect(
        color = if (darkThemeMode) Color(0xFF1C1C1E) else Color(0xFFFAFAFC)
    )
    val w = size.width
    val h = size.height
    val center = Offset(w / 2f, h / 2f)
    val radius = w * 0.41f
    drawCircle(
        brush = Brush.verticalGradient(listOf(Color(0xFF2CC5FF), Color(0xFF006AFF))),
        radius = radius,
        center = center
    )
    for (i in 0 until 36) {
        val angle = i * 10.0 * PI / 180.0
        val innerR = if (i % 3 == 0) radius * 0.76f else radius * 0.84f
        val outerR = radius * 0.92f
        val stroke = if (i % 3 == 0) w * 0.016f else w * 0.009f
        drawLine(
            color = Color.White.copy(alpha = 0.88f),
            start = Offset(center.x + (cos(angle) * innerR).toFloat(), center.y + (sin(angle) * innerR).toFloat()),
            end = Offset(center.x + (cos(angle) * outerR).toFloat(), center.y + (sin(angle) * outerR).toFloat()),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
    }
    rotate(degrees = 42f, pivot = center) {
        val northNeedle = Path().apply {
            moveTo(center.x, center.y - radius * 0.72f)
            lineTo(center.x + w * 0.075f, center.y)
            lineTo(center.x - w * 0.075f, center.y)
            close()
        }
        val southNeedle = Path().apply {
            moveTo(center.x, center.y + radius * 0.72f)
            lineTo(center.x + w * 0.075f, center.y)
            lineTo(center.x - w * 0.075f, center.y)
            close()
        }
        drawPath(northNeedle, Color(0xFFFF3B30))
        drawPath(southNeedle, Color.White)
    }
}

private fun DrawScope.drawMusicArtwork() {
    drawRect(
        brush = Brush.verticalGradient(listOf(Color(0xFFFB5D74), Color(0xFFFA233B)))
    )
    val w = size.width
    val h = size.height
    val notePath = Path().apply {
        moveTo(w * 0.40f, h * 0.28f)
        lineTo(w * 0.72f, h * 0.21f)
        lineTo(w * 0.72f, h * 0.30f)
        lineTo(w * 0.40f, h * 0.37f)
        close()
    }
    drawPath(notePath, Color.White)
    drawLine(Color.White, Offset(w * 0.42f, h * 0.31f), Offset(w * 0.42f, h * 0.67f), strokeWidth = w * 0.065f)
    drawLine(Color.White, Offset(w * 0.70f, h * 0.25f), Offset(w * 0.70f, h * 0.61f), strokeWidth = w * 0.065f)
    drawOval(Color.White, Offset(w * 0.26f, h * 0.60f), Size(w * 0.19f, h * 0.15f))
    drawOval(Color.White, Offset(w * 0.54f, h * 0.54f), Size(w * 0.19f, h * 0.15f))
}

private fun DrawScope.drawPhotosArtwork(darkThemeMode: Boolean) {
    drawRect(color = if (darkThemeMode) Color(0xFF1C1C1E) else Color.White)
    val center = Offset(size.width / 2f, size.height / 2f)
    val petalW = size.width * 0.22f
    val petalH = size.height * 0.34f
    val colors = listOf(
        Color(0xFFFF9500), Color(0xFFFFCC00),
        Color(0xFF7ED321), Color(0xFF34C759),
        Color(0xFF32ADE6), Color(0xFF007AFF),
        Color(0xFFAF52DE), Color(0xFFFF2D55)
    )
    colors.forEachIndexed { idx, color ->
        rotate(degrees = idx * 45f, pivot = center) {
            drawRoundRect(
                color = color.copy(alpha = 0.85f),
                topLeft = Offset(center.x - petalW / 2f, center.y - petalH - size.height * 0.045f),
                size = Size(petalW, petalH),
                cornerRadius = CornerRadius(petalW / 2f, petalW / 2f)
            )
        }
    }
}

private fun DrawScope.drawCameraArtwork() {
    drawRect(
        brush = Brush.verticalGradient(listOf(Color(0xFFE5E5EA), Color(0xFFB4B4BA)))
    )
    val w = size.width
    val h = size.height
    drawRoundRect(
        color = Color(0xFF2C2C2E),
        topLeft = Offset(w * 0.14f, h * 0.26f),
        size = Size(w * 0.72f, h * 0.50f),
        cornerRadius = CornerRadius(w * 0.09f, w * 0.09f)
    )
    val bump = Path().apply {
        moveTo(w * 0.33f, h * 0.26f)
        lineTo(w * 0.40f, h * 0.19f)
        lineTo(w * 0.60f, h * 0.19f)
        lineTo(w * 0.67f, h * 0.26f)
        close()
    }
    drawPath(bump, Color(0xFF2C2C2E))
    val lensCenter = Offset(w * 0.50f, h * 0.52f)
    drawCircle(Color(0xFF636366), radius = w * 0.19f, center = lensCenter)
    drawCircle(
        brush = Brush.radialGradient(
            listOf(Color(0xFF1F4E78), Color(0xFF0C1829), Color(0xFF05070B)),
            center = Offset(lensCenter.x - w * 0.04f, lensCenter.y - h * 0.04f),
            radius = w * 0.18f
        ),
        radius = w * 0.155f,
        center = lensCenter
    )
    drawCircle(Color(0xFF64D2FF).copy(alpha = 0.55f), radius = w * 0.042f, center = Offset(lensCenter.x - w * 0.045f, lensCenter.y - h * 0.045f))
    drawCircle(Color(0xFFFF9F0A), radius = w * 0.025f, center = Offset(w * 0.76f, h * 0.34f))
}

private fun DrawScope.drawSettingsArtwork() {
    drawRect(
        brush = Brush.verticalGradient(listOf(Color(0xFFE0E2E6), Color(0xFF8E9299)))
    )
    val w = size.width
    val center = Offset(w / 2f, w / 2f)
    drawCircle(Color(0xFF2C2C2E), radius = w * 0.37f, center = center)
    drawCircle(
        color = Color(0xFFD1D1D6),
        radius = w * 0.32f,
        center = center,
        style = Stroke(width = w * 0.055f)
    )
    for (i in 0 until 12) {
        rotate(degrees = i * 30f, pivot = center) {
            drawRoundRect(
                color = Color(0xFFE5E5EA),
                topLeft = Offset(center.x - w * 0.035f, center.y - w * 0.39f),
                size = Size(w * 0.07f, w * 0.09f),
                cornerRadius = CornerRadius(w * 0.02f, w * 0.02f)
            )
        }
    }
    for (i in 0 until 3) {
        rotate(degrees = i * 120f - 30f, pivot = center) {
            drawRoundRect(
                color = Color(0xFFAEAEB2),
                topLeft = Offset(center.x - w * 0.03f, center.y - w * 0.27f),
                size = Size(w * 0.06f, w * 0.27f),
                cornerRadius = CornerRadius(w * 0.02f, w * 0.02f)
            )
        }
    }
    drawCircle(Color(0xFF8E8E93), radius = w * 0.13f, center = center)
    drawCircle(Color(0xFF3A3A3C), radius = w * 0.065f, center = center)
}

@Composable
private fun IosCalendarArtwork(iconSize: Dp, darkThemeMode: Boolean, modifier: Modifier) {
    val now = remember { Date() }
    val weekday = remember(now) { SimpleDateFormat("EEE", Locale.ENGLISH).format(now) }
    val day = remember(now) { SimpleDateFormat("d", Locale.ENGLISH).format(now) }
    val bg = if (darkThemeMode) Color(0xFF1C1C1E) else Color.White
    val fg = if (darkThemeMode) Color.White else Color(0xFF1C1C1E)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bg)
            .padding(top = iconSize * 0.08f),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = weekday,
                color = Color(0xFFFF3B30),
                fontFamily = IosSansFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = (iconSize.value * 0.19f).sp,
                lineHeight = (iconSize.value * 0.21f).sp
            )
            Text(
                text = day,
                color = fg,
                fontFamily = IosSansFontFamily,
                fontWeight = FontWeight.Light,
                fontSize = (iconSize.value * 0.50f).sp,
                lineHeight = (iconSize.value * 0.52f).sp
            )
        }
    }
}

private fun DrawScope.drawWeatherArtwork() {
    drawRect(
        brush = Brush.verticalGradient(listOf(Color(0xFF47B3FF), Color(0xFF1D69E4)))
    )
    val w = size.width
    val h = size.height
    drawCircle(
        brush = Brush.radialGradient(listOf(Color(0xFFFFEA59), Color(0xFFFFB800))),
        radius = w * 0.19f,
        center = Offset(w * 0.38f, h * 0.42f)
    )
    drawCircle(Color.White.copy(alpha = 0.94f), radius = w * 0.17f, center = Offset(w * 0.43f, h * 0.58f))
    drawCircle(Color.White.copy(alpha = 0.96f), radius = w * 0.20f, center = Offset(w * 0.58f, h * 0.54f))
    drawCircle(Color.White.copy(alpha = 0.94f), radius = w * 0.14f, center = Offset(w * 0.72f, h * 0.61f))
    drawRoundRect(
        color = Color.White.copy(alpha = 0.96f),
        topLeft = Offset(w * 0.26f, h * 0.55f),
        size = Size(w * 0.58f, h * 0.20f),
        cornerRadius = CornerRadius(w * 0.10f, w * 0.10f)
    )
}

@Composable
private fun IosClockArtwork(iconSize: Dp, modifier: Modifier) {
    var timeMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            timeMillis = System.currentTimeMillis()
            delay(1000L)
        }
    }
    val cal = remember(timeMillis) { Calendar.getInstance().apply { timeInMillis = timeMillis } }
    val sec = cal.get(Calendar.SECOND)
    val min = cal.get(Calendar.MINUTE)
    val hour = cal.get(Calendar.HOUR)

    Box(modifier = modifier.fillMaxSize().background(Color(0xFF1C1C1E))) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val center = Offset(w / 2f, w / 2f)
            val dialR = w * 0.41f
            drawCircle(Color(0xFFF9F9FB), radius = dialR, center = center)
            for (i in 0 until 12) {
                val angle = i * 30.0 * PI / 180.0
                drawLine(
                    color = Color(0xFF1C1C1E),
                    start = Offset(center.x + (cos(angle) * dialR * 0.78f).toFloat(), center.y + (sin(angle) * dialR * 0.78f).toFloat()),
                    end = Offset(center.x + (cos(angle) * dialR * 0.90f).toFloat(), center.y + (sin(angle) * dialR * 0.90f).toFloat()),
                    strokeWidth = w * 0.022f,
                    cap = StrokeCap.Round
                )
            }
            val hourAngle = ((hour + min / 60f) * 30f - 90f) * PI / 180.0
            drawLine(
                color = Color(0xFF1C1C1E),
                start = center,
                end = Offset(center.x + (cos(hourAngle) * dialR * 0.50f).toFloat(), center.y + (sin(hourAngle) * dialR * 0.50f).toFloat()),
                strokeWidth = w * 0.038f,
                cap = StrokeCap.Round
            )
            val minAngle = ((min + sec / 60f) * 6f - 90f) * PI / 180.0
            drawLine(
                color = Color(0xFF1C1C1E),
                start = center,
                end = Offset(center.x + (cos(minAngle) * dialR * 0.74f).toFloat(), center.y + (sin(minAngle) * dialR * 0.74f).toFloat()),
                strokeWidth = w * 0.026f,
                cap = StrokeCap.Round
            )
            val secAngle = (sec * 6f - 90f) * PI / 180.0
            drawLine(
                color = Color(0xFFFF9500),
                start = center,
                end = Offset(center.x + (cos(secAngle) * dialR * 0.80f).toFloat(), center.y + (sin(secAngle) * dialR * 0.80f).toFloat()),
                strokeWidth = w * 0.015f,
                cap = StrokeCap.Round
            )
            drawCircle(Color(0xFFFF9500), radius = w * 0.030f, center = center)
        }
    }
}

private fun DrawScope.drawMapsArtwork() {
    val w = size.width
    val h = size.height
    drawRect(Color(0xFFF2EFE9))
    drawRect(Color(0xFF7AD975), topLeft = Offset(w * 0.55f, 0f), size = Size(w * 0.45f, h * 0.58f))
    drawRect(Color(0xFFFFD54F), topLeft = Offset(0f, h * 0.50f), size = Size(w, h * 0.14f))
    drawRect(Color(0xFF90CAF9), topLeft = Offset(w * 0.14f, 0f), size = Size(w * 0.15f, h))
    val road = Path().apply {
        moveTo(w * 0.30f, h)
        lineTo(w * 0.48f, h * 0.48f)
        lineTo(w * 0.88f, h * 0.14f)
    }
    drawPath(road, Color.White, style = Stroke(width = w * 0.13f, cap = StrokeCap.Round))
    drawPath(road, Color(0xFF007AFF), style = Stroke(width = w * 0.07f, cap = StrokeCap.Round))
    val badgeCenter = Offset(w * 0.48f, h * 0.50f)
    drawCircle(Color.White, radius = w * 0.22f, center = badgeCenter)
    drawCircle(Color(0xFF007AFF), radius = w * 0.18f, center = badgeCenter)
    val arrow = Path().apply {
        moveTo(badgeCenter.x, badgeCenter.y - w * 0.10f)
        lineTo(badgeCenter.x + w * 0.08f, badgeCenter.y + w * 0.08f)
        lineTo(badgeCenter.x, badgeCenter.y + w * 0.03f)
        lineTo(badgeCenter.x - w * 0.08f, badgeCenter.y + w * 0.08f)
        close()
    }
    drawPath(arrow, Color.White)
}

private fun DrawScope.drawAppStoreArtwork() {
    drawRect(brush = Brush.verticalGradient(listOf(Color(0xFF19B5FA), Color(0xFF0D64E2))))
    val w = size.width
    val h = size.height
    val stroke = w * 0.085f
    drawLine(Color.White, Offset(w * 0.50f, h * 0.22f), Offset(w * 0.26f, h * 0.72f), strokeWidth = stroke, cap = StrokeCap.Round)
    drawLine(Color.White, Offset(w * 0.50f, h * 0.22f), Offset(w * 0.74f, h * 0.72f), strokeWidth = stroke, cap = StrokeCap.Round)
    drawLine(Color.White, Offset(w * 0.24f, h * 0.58f), Offset(w * 0.76f, h * 0.58f), strokeWidth = stroke, cap = StrokeCap.Round)
}

private fun DrawScope.drawFaceTimeArtwork() {
    drawRect(brush = Brush.verticalGradient(listOf(Color(0xFF63F079), Color(0xFF28CD41))))
    val w = size.width
    val h = size.height
    drawRoundRect(
        color = Color.White,
        topLeft = Offset(w * 0.18f, h * 0.30f),
        size = Size(w * 0.44f, h * 0.40f),
        cornerRadius = CornerRadius(w * 0.08f, w * 0.08f)
    )
    val lens = Path().apply {
        moveTo(w * 0.64f, h * 0.43f)
        lineTo(w * 0.82f, h * 0.33f)
        lineTo(w * 0.82f, h * 0.67f)
        lineTo(w * 0.64f, h * 0.57f)
        close()
    }
    drawPath(lens, Color.White)
}

private fun DrawScope.drawPodcastsArtwork() {
    drawRect(brush = Brush.verticalGradient(listOf(Color(0xFFC462F7), Color(0xFF7D1FA8))))
    val w = size.width
    val center = Offset(w * 0.50f, w * 0.46f)
    drawArc(
        color = Color.White.copy(alpha = 0.85f),
        startAngle = 145f,
        sweepAngle = 250f,
        useCenter = false,
        topLeft = Offset(center.x - w * 0.30f, center.y - w * 0.30f),
        size = Size(w * 0.60f, w * 0.60f),
        style = Stroke(width = w * 0.045f, cap = StrokeCap.Round)
    )
    drawArc(
        color = Color.White.copy(alpha = 0.92f),
        startAngle = 140f,
        sweepAngle = 260f,
        useCenter = false,
        topLeft = Offset(center.x - w * 0.19f, center.y - w * 0.19f),
        size = Size(w * 0.38f, w * 0.38f),
        style = Stroke(width = w * 0.045f, cap = StrokeCap.Round)
    )
    drawCircle(Color.White, radius = w * 0.07f, center = center)
    drawRoundRect(
        color = Color.White,
        topLeft = Offset(center.x - w * 0.065f, center.y + w * 0.10f),
        size = Size(w * 0.13f, w * 0.25f),
        cornerRadius = CornerRadius(w * 0.065f, w * 0.065f)
    )
}

private fun DrawScope.drawHealthArtwork(darkThemeMode: Boolean) {
    drawRect(color = if (darkThemeMode) Color(0xFF1C1C1E) else Color.White)
    val w = size.width
    val h = size.height
    val cx = w * 0.66f
    val cy = h * 0.35f
    val s = w * 0.19f
    val heart = Path().apply {
        moveTo(cx, cy + s * 0.85f)
        cubicTo(cx - s * 1.4f, cy + s * 0.1f, cx - s * 1.1f, cy - s * 1.1f, cx, cy - s * 0.35f)
        cubicTo(cx + s * 1.1f, cy - s * 1.1f, cx + s * 1.4f, cy + s * 0.1f, cx, cy + s * 0.85f)
        close()
    }
    drawPath(
        path = heart,
        brush = Brush.verticalGradient(listOf(Color(0xFFFF5C87), Color(0xFFFF2D55)))
    )
}
