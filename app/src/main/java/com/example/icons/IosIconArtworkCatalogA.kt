package com.example.icons

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
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
        brush = Brush.verticalGradient(listOf(Color(0xFF72F57E), Color(0xFF1EC83A)))
    )
    val w = size.width
    val h = size.height
    val handset = Path().apply {
        moveTo(w * 0.28f, h * 0.22f)
        cubicTo(w * 0.34f, h * 0.19f, w * 0.42f, h * 0.24f, w * 0.45f, h * 0.32f)
        cubicTo(w * 0.47f, h * 0.37f, w * 0.42f, h * 0.40f, w * 0.41f, h * 0.43f)
        cubicTo(w * 0.43f, h * 0.50f, w * 0.50f, h * 0.57f, w * 0.57f, h * 0.59f)
        cubicTo(w * 0.60f, h * 0.58f, w * 0.63f, h * 0.53f, w * 0.68f, h * 0.55f)
        cubicTo(w * 0.76f, h * 0.58f, w * 0.81f, h * 0.66f, w * 0.78f, h * 0.72f)
        cubicTo(w * 0.74f, h * 0.79f, w * 0.61f, h * 0.80f, w * 0.47f, h * 0.71f)
        cubicTo(w * 0.34f, h * 0.62f, w * 0.26f, h * 0.52f, w * 0.22f, h * 0.38f)
        cubicTo(w * 0.20f, h * 0.30f, w * 0.23f, h * 0.24f, w * 0.28f, h * 0.22f)
        close()
    }
    drawPath(handset, color = Color.White)
}

private fun DrawScope.drawMessagesArtwork() {
    drawRect(
        brush = Brush.verticalGradient(listOf(Color(0xFF72F57E), Color(0xFF1EC83A)))
    )
    val w = size.width
    val h = size.height
    drawOval(
        brush = Brush.verticalGradient(listOf(Color.White, Color(0xFFF0F4F1))),
        topLeft = Offset(w * 0.14f, h * 0.19f),
        size = Size(w * 0.72f, h * 0.55f)
    )
    val tail = Path().apply {
        moveTo(w * 0.26f, h * 0.62f)
        cubicTo(w * 0.25f, h * 0.70f, w * 0.20f, h * 0.76f, w * 0.16f, h * 0.79f)
        cubicTo(w * 0.25f, h * 0.78f, w * 0.34f, h * 0.74f, w * 0.39f, h * 0.70f)
        close()
    }
    drawPath(tail, color = Color(0xFFF3F6F4))
}

private fun DrawScope.drawSafariArtwork(darkThemeMode: Boolean) {
    drawRect(
        brush = Brush.verticalGradient(
            if (darkThemeMode) listOf(Color(0xFF262629), Color(0xFF18181A))
            else listOf(Color(0xFFFFFFFF), Color(0xFFECECEE))
        )
    )
    val w = size.width
    val center = Offset(w / 2f, w / 2f)
    val radius = w * 0.42f
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFF2CD0FF), Color(0xFF1492FF), Color(0xFF005BEA)),
            center = Offset(center.x, center.y - radius * 0.2f),
            radius = radius * 1.25f
        ),
        radius = radius,
        center = center
    )
    for (i in 0 until 72) {
        val angle = i * 5.0 * PI / 180.0
        val major = i % 2 == 0
        val innerR = if (major) radius * 0.79f else radius * 0.87f
        val outerR = radius * 0.94f
        drawLine(
            color = Color.White.copy(alpha = if (major) 0.92f else 0.72f),
            start = Offset(center.x + (cos(angle) * innerR).toFloat(), center.y + (sin(angle) * innerR).toFloat()),
            end = Offset(center.x + (cos(angle) * outerR).toFloat(), center.y + (sin(angle) * outerR).toFloat()),
            strokeWidth = if (major) w * 0.015f else w * 0.009f,
            cap = StrokeCap.Round
        )
    }
    rotate(degrees = 45f, pivot = center) {
        val tipY = center.y - radius * 0.82f
        val botY = center.y + radius * 0.82f
        val halfW = w * 0.072f
        // Split-shaded 3D compass needle
        val redLeft = Path().apply {
            moveTo(center.x, tipY)
            lineTo(center.x, center.y)
            lineTo(center.x - halfW, center.y)
            close()
        }
        val redRight = Path().apply {
            moveTo(center.x, tipY)
            lineTo(center.x + halfW, center.y)
            lineTo(center.x, center.y)
            close()
        }
        val whiteLeft = Path().apply {
            moveTo(center.x - halfW, center.y)
            lineTo(center.x, center.y)
            lineTo(center.x, botY)
            close()
        }
        val whiteRight = Path().apply {
            moveTo(center.x, center.y)
            lineTo(center.x + halfW, center.y)
            lineTo(center.x, botY)
            close()
        }
        drawPath(redLeft, Color(0xFFFF453A))
        drawPath(redRight, Color(0xFFD62820))
        drawPath(whiteLeft, Color.White)
        drawPath(whiteRight, Color(0xFFD9DADF))
        drawCircle(Color(0xFFC7C7CC), radius = w * 0.020f, center = center)
    }
}

private fun DrawScope.drawMusicArtwork() {
    drawRect(
        brush = Brush.verticalGradient(listOf(Color(0xFFFC5C74), Color(0xFFFA233B)))
    )
    val w = size.width
    val h = size.height
    val beam = Path().apply {
        moveTo(w * 0.36f, h * 0.27f)
        lineTo(w * 0.74f, h * 0.19f)
        lineTo(w * 0.74f, h * 0.30f)
        lineTo(w * 0.36f, h * 0.38f)
        close()
    }
    drawPath(beam, Color.White)
    drawLine(Color.White, Offset(w * 0.39f, h * 0.31f), Offset(w * 0.39f, h * 0.69f), strokeWidth = w * 0.068f)
    drawLine(Color.White, Offset(w * 0.71f, h * 0.24f), Offset(w * 0.71f, h * 0.62f), strokeWidth = w * 0.068f)
    rotate(-14f, pivot = Offset(w * 0.31f, h * 0.69f)) {
        drawOval(Color.White, Offset(w * 0.21f, h * 0.62f), Size(w * 0.21f, h * 0.15f))
    }
    rotate(-14f, pivot = Offset(w * 0.63f, h * 0.62f)) {
        drawOval(Color.White, Offset(w * 0.53f, h * 0.55f), Size(w * 0.21f, h * 0.15f))
    }
}

private fun DrawScope.drawPhotosArtwork(darkThemeMode: Boolean) {
    drawRect(color = if (darkThemeMode) Color(0xFF1C1C1E) else Color.White)
    val center = Offset(size.width / 2f, size.height / 2f)
    val petalW = size.width * 0.23f
    val petalH = size.height * 0.35f
    val petals = listOf(
        Color(0xFFFF9F0A),
        Color(0xFFFFD60A),
        Color(0xFFA4DE02),
        Color(0xFF34C759),
        Color(0xFF32ADE6),
        Color(0xFF007AFF),
        Color(0xFFAF52DE),
        Color(0xFFFF2D55)
    )
    petals.forEachIndexed { idx, color ->
        rotate(degrees = idx * 45f, pivot = center) {
            drawRoundRect(
                color = color.copy(alpha = 0.85f),
                topLeft = Offset(center.x - petalW / 2f, center.y - petalH - size.height * 0.045f),
                size = Size(petalW, petalH),
                cornerRadius = CornerRadius(petalW / 2f, petalW / 2f)
            )
        }
    }
    drawCircle(
        color = if (darkThemeMode) Color(0xFF1C1C1E) else Color.White,
        radius = size.width * 0.055f,
        center = center
    )
}

private fun DrawScope.drawCameraArtwork() {
    drawRect(
        brush = Brush.verticalGradient(listOf(Color(0xFFF2F2F7), Color(0xFFC2C2C7)))
    )
    val w = size.width
    val h = size.height
    drawRoundRect(
        brush = Brush.verticalGradient(listOf(Color(0xFF424245), Color(0xFF232325))),
        topLeft = Offset(w * 0.13f, h * 0.26f),
        size = Size(w * 0.74f, h * 0.51f),
        cornerRadius = CornerRadius(w * 0.095f, w * 0.095f)
    )
    val topHump = Path().apply {
        moveTo(w * 0.31f, h * 0.27f)
        lineTo(w * 0.37f, h * 0.18f)
        lineTo(w * 0.63f, h * 0.18f)
        lineTo(w * 0.69f, h * 0.27f)
        close()
    }
    drawPath(topHump, Color(0xFF3A3A3C))
    // Shutter accent & flash
    drawRoundRect(
        color = Color(0xFF5A5A5E),
        topLeft = Offset(w * 0.20f, h * 0.21f),
        size = Size(w * 0.08f, h * 0.05f),
        cornerRadius = CornerRadius(w * 0.02f, w * 0.02f)
    )
    drawCircle(Color(0xFFFFB340), radius = w * 0.024f, center = Offset(w * 0.78f, h * 0.34f))
    val lensCenter = Offset(w * 0.50f, h * 0.52f)
    drawCircle(Color(0xFF8E8E93), radius = w * 0.205f, center = lensCenter)
    drawCircle(Color(0xFF1C1C1E), radius = w * 0.182f, center = lensCenter)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFF2874C5), Color(0xFF0F2647), Color(0xFF040810)),
            center = Offset(lensCenter.x - w * 0.04f, lensCenter.y - h * 0.04f),
            radius = w * 0.16f
        ),
        radius = w * 0.135f,
        center = lensCenter
    )
    drawCircle(Color(0xFF04070D), radius = w * 0.068f, center = lensCenter)
    drawCircle(
        color = Color(0xFF70D7FF).copy(alpha = 0.65f),
        radius = w * 0.036f,
        center = Offset(lensCenter.x - w * 0.042f, lensCenter.y - h * 0.042f)
    )
}

private fun DrawScope.drawSettingsArtwork() {
    drawRect(
        brush = Brush.verticalGradient(listOf(Color(0xFFE3E5EA), Color(0xFF8E929B)))
    )
    val w = size.width
    val center = Offset(w / 2f, w / 2f)
    // Dark recessed interior
    drawCircle(Color(0xFF2E2E32), radius = w * 0.38f, center = center)
    // Outer toothed metallic ring
    for (i in 0 until 24) {
        rotate(degrees = i * 15f, pivot = center) {
            drawRoundRect(
                color = Color(0xFFDCDDE2),
                topLeft = Offset(center.x - w * 0.022f, center.y - w * 0.42f),
                size = Size(w * 0.044f, w * 0.08f),
                cornerRadius = CornerRadius(w * 0.012f, w * 0.012f)
            )
        }
    }
    drawCircle(
        brush = Brush.verticalGradient(listOf(Color(0xFFF2F2F7), Color(0xFFA0A3AA))),
        radius = w * 0.345f,
        center = center,
        style = Stroke(width = w * 0.058f)
    )
    // 3 structural spokes
    for (i in 0 until 3) {
        rotate(degrees = i * 120f - 30f, pivot = center) {
            drawRoundRect(
                brush = Brush.verticalGradient(listOf(Color(0xFFD1D1D6), Color(0xFF98989E))),
                topLeft = Offset(center.x - w * 0.035f, center.y - w * 0.31f),
                size = Size(w * 0.07f, w * 0.31f),
                cornerRadius = CornerRadius(w * 0.02f, w * 0.02f)
            )
        }
    }
    // Inner notched gear hub
    drawCircle(
        color = Color(0xFFE5E5EA),
        radius = w * 0.19f,
        center = center,
        style = Stroke(width = w * 0.035f)
    )
    drawCircle(
        brush = Brush.verticalGradient(listOf(Color(0xFFE5E5EA), Color(0xFF9A9CA3))),
        radius = w * 0.11f,
        center = center
    )
    drawCircle(Color(0xFF3A3A3D), radius = w * 0.045f, center = center)
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
            .padding(top = iconSize * 0.09f),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = weekday,
                color = Color(0xFFFF3B30),
                fontFamily = IosSansFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = (iconSize.value * 0.195f).sp,
                lineHeight = (iconSize.value * 0.21f).sp
            )
            Text(
                text = day,
                color = fg,
                fontFamily = IosSansFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = (iconSize.value * 0.50f).sp,
                lineHeight = (iconSize.value * 0.52f).sp
            )
        }
    }
}

private fun DrawScope.drawWeatherArtwork() {
    drawRect(
        brush = Brush.verticalGradient(listOf(Color(0xFF3FB2FF), Color(0xFF1664E0)))
    )
    val w = size.width
    val h = size.height
    drawCircle(
        brush = Brush.radialGradient(listOf(Color(0xFFFFED63), Color(0xFFFFB500))),
        radius = w * 0.195f,
        center = Offset(w * 0.37f, h * 0.41f)
    )
    drawCircle(Color.White.copy(alpha = 0.94f), radius = w * 0.165f, center = Offset(w * 0.41f, h * 0.58f))
    drawCircle(Color.White.copy(alpha = 0.97f), radius = w * 0.205f, center = Offset(w * 0.57f, h * 0.53f))
    drawCircle(Color.White.copy(alpha = 0.95f), radius = w * 0.145f, center = Offset(w * 0.73f, h * 0.61f))
    drawRoundRect(
        brush = Brush.verticalGradient(listOf(Color.White, Color(0xFFE6F1FC))),
        topLeft = Offset(w * 0.24f, h * 0.54f),
        size = Size(w * 0.61f, h * 0.21f),
        cornerRadius = CornerRadius(w * 0.105f, w * 0.105f)
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
    val textMeasurer = rememberTextMeasurer()

    Box(modifier = modifier.fillMaxSize().background(Color(0xFF1C1C1E))) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val center = Offset(w / 2f, w / 2f)
            val dialR = w * 0.42f
            drawCircle(Color(0xFFFAFAFC), radius = dialR, center = center)
            for (i in 0 until 60) {
                val angle = i * 6.0 * PI / 180.0
                val isHour = i % 5 == 0
                val inner = if (isHour) dialR * 0.85f else dialR * 0.89f
                val outer = dialR * 0.95f
                drawLine(
                    color = if (isHour) Color(0xFF1C1C1E) else Color(0xFFB5B5BA),
                    start = Offset(center.x + (cos(angle) * inner).toFloat(), center.y + (sin(angle) * inner).toFloat()),
                    end = Offset(center.x + (cos(angle) * outer).toFloat(), center.y + (sin(angle) * outer).toFloat()),
                    strokeWidth = if (isHour) w * 0.018f else w * 0.009f,
                    cap = StrokeCap.Round
                )
            }
            val numStyle = TextStyle(
                color = Color(0xFF1C1C1E),
                fontFamily = IosSansFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = (iconSize.value * 0.135f).sp
            )
            listOf("12" to -90.0, "3" to 0.0, "6" to 90.0, "9" to 180.0).forEach { (label, deg) ->
                val rad = deg * PI / 180.0
                val res = textMeasurer.measure(label, numStyle)
                val rNum = dialR * 0.65f
                val tx = center.x + (cos(rad) * rNum).toFloat() - res.size.width / 2f
                val ty = center.y + (sin(rad) * rNum).toFloat() - res.size.height / 2f
                drawText(res, topLeft = Offset(tx, ty))
            }

            val hourAngle = ((hour + min / 60f) * 30f - 90f) * PI / 180.0
            drawLine(
                color = Color(0xFF1C1C1E),
                start = center,
                end = Offset(center.x + (cos(hourAngle) * dialR * 0.48f).toFloat(), center.y + (sin(hourAngle) * dialR * 0.48f).toFloat()),
                strokeWidth = w * 0.042f,
                cap = StrokeCap.Round
            )
            val minAngle = ((min + sec / 60f) * 6f - 90f) * PI / 180.0
            drawLine(
                color = Color(0xFF1C1C1E),
                start = center,
                end = Offset(center.x + (cos(minAngle) * dialR * 0.74f).toFloat(), center.y + (sin(minAngle) * dialR * 0.74f).toFloat()),
                strokeWidth = w * 0.028f,
                cap = StrokeCap.Round
            )
            val secAngle = (sec * 6f - 90f) * PI / 180.0
            drawLine(
                color = Color(0xFFFF9500),
                start = Offset(center.x - (cos(secAngle) * dialR * 0.15f).toFloat(), center.y - (sin(secAngle) * dialR * 0.15f).toFloat()),
                end = Offset(center.x + (cos(secAngle) * dialR * 0.82f).toFloat(), center.y + (sin(secAngle) * dialR * 0.82f).toFloat()),
                strokeWidth = w * 0.015f,
                cap = StrokeCap.Round
            )
            drawCircle(Color(0xFFFF9500), radius = w * 0.028f, center = center)
        }
    }
}

private fun DrawScope.drawMapsArtwork() {
    val w = size.width
    val h = size.height
    drawRect(Color(0xFFF4F1EA))
    drawRect(Color(0xFF7BE074), topLeft = Offset(w * 0.48f, 0f), size = Size(w * 0.52f, h * 0.55f))
    drawRect(Color(0xFF9ED4FC), topLeft = Offset(0f, 0f), size = Size(w * 0.24f, h))
    drawRect(Color(0xFFFFDF59), topLeft = Offset(0f, h * 0.58f), size = Size(w, h * 0.16f))
    // White-cased blue highway
    val highway = Path().apply {
        moveTo(w * 0.30f, h)
        lineTo(w * 0.44f, h * 0.46f)
        lineTo(w * 0.85f, h * 0.08f)
    }
    drawPath(highway, Color.White, style = Stroke(width = w * 0.15f, cap = StrokeCap.Round))
    drawPath(highway, Color(0xFF288BFF), style = Stroke(width = w * 0.09f, cap = StrokeCap.Round))
    // Iconic circular white + blue navigation pointer
    val badgeCenter = Offset(w * 0.44f, h * 0.52f)
    drawCircle(Color.White, radius = w * 0.24f, center = badgeCenter)
    drawCircle(Color(0xFF007AFF), radius = w * 0.19f, center = badgeCenter)
    val arrow = Path().apply {
        moveTo(badgeCenter.x, badgeCenter.y - w * 0.11f)
        lineTo(badgeCenter.x + w * 0.085f, badgeCenter.y + w * 0.09f)
        lineTo(badgeCenter.x, badgeCenter.y + w * 0.035f)
        lineTo(badgeCenter.x - w * 0.085f, badgeCenter.y + w * 0.09f)
        close()
    }
    drawPath(arrow, Color.White)
}

private fun DrawScope.drawAppStoreArtwork() {
    drawRect(brush = Brush.verticalGradient(listOf(Color(0xFF1BB7FC), Color(0xFF0C63E2))))
    val w = size.width
    val h = size.height
    val stroke = w * 0.092f
    drawLine(Color.White, Offset(w * 0.46f, h * 0.22f), Offset(w * 0.74f, h * 0.74f), strokeWidth = stroke, cap = StrokeCap.Round)
    drawLine(Color(0xFF0C63E2).copy(alpha = 0.25f), Offset(w * 0.54f, h * 0.22f), Offset(w * 0.26f, h * 0.74f), strokeWidth = stroke * 1.18f, cap = StrokeCap.Round)
    drawLine(Color.White, Offset(w * 0.54f, h * 0.22f), Offset(w * 0.26f, h * 0.74f), strokeWidth = stroke, cap = StrokeCap.Round)
    drawLine(Color.White, Offset(w * 0.22f, h * 0.60f), Offset(w * 0.78f, h * 0.60f), strokeWidth = stroke, cap = StrokeCap.Round)
}

private fun DrawScope.drawFaceTimeArtwork() {
    drawRect(brush = Brush.verticalGradient(listOf(Color(0xFF6CF27A), Color(0xFF21C73C))))
    val w = size.width
    val h = size.height
    drawRoundRect(
        color = Color.White,
        topLeft = Offset(w * 0.16f, h * 0.28f),
        size = Size(w * 0.46f, h * 0.44f),
        cornerRadius = CornerRadius(w * 0.09f, w * 0.09f)
    )
    val lens = Path().apply {
        moveTo(w * 0.64f, h * 0.42f)
        lineTo(w * 0.82f, h * 0.30f)
        lineTo(w * 0.84f, h * 0.32f)
        lineTo(w * 0.84f, h * 0.68f)
        lineTo(w * 0.82f, h * 0.70f)
        lineTo(w * 0.64f, h * 0.58f)
        close()
    }
    drawPath(lens, Color.White)
}

private fun DrawScope.drawPodcastsArtwork() {
    drawRect(brush = Brush.verticalGradient(listOf(Color(0xFFC966F8), Color(0xFF7B1DA6))))
    val w = size.width
    val center = Offset(w * 0.50f, w * 0.45f)
    drawArc(
        color = Color.White.copy(alpha = 0.82f),
        startAngle = 142f,
        sweepAngle = 256f,
        useCenter = false,
        topLeft = Offset(center.x - w * 0.32f, center.y - w * 0.32f),
        size = Size(w * 0.64f, w * 0.64f),
        style = Stroke(width = w * 0.045f, cap = StrokeCap.Round)
    )
    drawArc(
        color = Color.White.copy(alpha = 0.92f),
        startAngle = 135f,
        sweepAngle = 270f,
        useCenter = false,
        topLeft = Offset(center.x - w * 0.20f, center.y - w * 0.20f),
        size = Size(w * 0.40f, w * 0.40f),
        style = Stroke(width = w * 0.045f, cap = StrokeCap.Round)
    )
    drawCircle(Color.White, radius = w * 0.075f, center = center)
    val torso = Path().apply {
        moveTo(center.x - w * 0.07f, center.y + w * 0.11f)
        cubicTo(center.x - w * 0.07f, center.y + w * 0.08f, center.x + w * 0.07f, center.y + w * 0.08f, center.x + w * 0.07f, center.y + w * 0.11f)
        lineTo(center.x + w * 0.055f, center.y + w * 0.35f)
        cubicTo(center.x + w * 0.04f, center.y + w * 0.39f, center.x - w * 0.04f, center.y + w * 0.39f, center.x - w * 0.055f, center.y + w * 0.35f)
        close()
    }
    drawPath(torso, Color.White)
}

private fun DrawScope.drawHealthArtwork(darkThemeMode: Boolean) {
    drawRect(color = if (darkThemeMode) Color(0xFF1C1C1E) else Color.White)
    val w = size.width
    val h = size.height
    val cx = w * 0.67f
    val cy = h * 0.34f
    val s = w * 0.185f
    val heart = Path().apply {
        moveTo(cx, cy + s * 0.85f)
        cubicTo(cx - s * 1.4f, cy + s * 0.1f, cx - s * 1.1f, cy - s * 1.1f, cx, cy - s * 0.35f)
        cubicTo(cx + s * 1.1f, cy - s * 1.1f, cx + s * 1.4f, cy + s * 0.1f, cx, cy + s * 0.85f)
        close()
    }
    drawPath(
        path = heart,
        brush = Brush.verticalGradient(listOf(Color(0xFFFF5E89), Color(0xFFFF2D55)))
    )
}
