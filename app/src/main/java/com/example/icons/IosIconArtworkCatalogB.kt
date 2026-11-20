package com.example.icons

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.sp
import com.example.ui.theme.IosSansFontFamily

@Composable
fun DrawIosIconCatalogB(
    key: IosIconKey,
    darkThemeMode: Boolean,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()
    Canvas(modifier = modifier.fillMaxSize()) {
        when (key) {
            IosIconKey.WALLET -> drawWalletArtwork()
            IosIconKey.NOTES -> drawNotesArtwork(darkThemeMode)
            IosIconKey.REMINDERS -> drawRemindersArtwork(darkThemeMode)
            IosIconKey.TV -> drawTvArtwork(textMeasurer)
            IosIconKey.FILES -> drawFilesArtwork(darkThemeMode)
            IosIconKey.MAIL -> drawMailArtwork()
            IosIconKey.CONTACTS -> drawContactsArtwork()
            IosIconKey.CALCULATOR -> drawCalculatorArtwork()
            IosIconKey.GOOGLE -> drawGoogleArtwork(darkThemeMode)
            IosIconKey.YOUTUBE -> drawYouTubeArtwork(darkThemeMode)
            IosIconKey.WHATSAPP -> drawWhatsAppArtwork()
            IosIconKey.INSTAGRAM -> drawInstagramArtwork()
            IosIconKey.TIKTOK -> drawTikTokArtwork()
            IosIconKey.CHROME -> drawChromeArtwork(darkThemeMode)
            IosIconKey.HOME -> drawHomeArtwork(darkThemeMode)
            IosIconKey.SPOTIFY -> drawSpotifyArtwork()
            IosIconKey.TELEGRAM -> drawTelegramArtwork()
            IosIconKey.FACEBOOK -> drawFacebookArtwork()
            IosIconKey.X_TWITTER -> drawXArtwork()
            else -> Unit
        }
    }
}

private fun DrawScope.drawWalletArtwork() {
    drawRect(Color(0xFF1C1C1E))
    val w = size.width
    val h = size.height
    drawRoundRect(Color(0xFF2C2C2E), Offset(w * 0.13f, h * 0.17f), Size(w * 0.74f, h * 0.66f), CornerRadius(w * 0.11f, w * 0.11f))
    drawRoundRect(Color(0xFF34AADC), Offset(w * 0.17f, h * 0.22f), Size(w * 0.66f, h * 0.22f), CornerRadius(w * 0.055f, w * 0.055f))
    drawRoundRect(Color(0xFFFFB800), Offset(w * 0.17f, h * 0.29f), Size(w * 0.66f, h * 0.22f), CornerRadius(w * 0.055f, w * 0.055f))
    drawRoundRect(Color(0xFF4CD964), Offset(w * 0.17f, h * 0.36f), Size(w * 0.66f, h * 0.22f), CornerRadius(w * 0.055f, w * 0.055f))
    drawRoundRect(Color(0xFFFF6951), Offset(w * 0.17f, h * 0.43f), Size(w * 0.66f, h * 0.22f), CornerRadius(w * 0.055f, w * 0.055f))
    val pocket = Path().apply {
        moveTo(w * 0.13f, h * 0.50f)
        lineTo(w * 0.37f, h * 0.50f)
        cubicTo(w * 0.42f, h * 0.59f, w * 0.58f, h * 0.59f, w * 0.63f, h * 0.50f)
        lineTo(w * 0.87f, h * 0.50f)
        lineTo(w * 0.87f, h * 0.74f)
        cubicTo(w * 0.87f, h * 0.81f, w * 0.82f, h * 0.83f, w * 0.76f, h * 0.83f)
        lineTo(w * 0.24f, h * 0.83f)
        cubicTo(w * 0.18f, h * 0.83f, w * 0.13f, h * 0.81f, w * 0.13f, h * 0.74f)
        close()
    }
    drawPath(pocket, Brush.verticalGradient(listOf(Color(0xFFE0D7CA), Color(0xFFC2B7A7))))
}

private fun DrawScope.drawNotesArtwork(darkThemeMode: Boolean) {
    drawRect(if (darkThemeMode) Color(0xFF1C1C1E) else Color(0xFFFCFCFE))
    val w = size.width
    val h = size.height
    drawRect(
        brush = Brush.verticalGradient(listOf(Color(0xFFFFDB43), Color(0xFFFFC000))),
        size = Size(w, h * 0.27f)
    )
    // Perforated dotted tear line
    var x = w * 0.04f
    while (x < w * 0.96f) {
        drawCircle(Color(0xFF8E8E93).copy(alpha = 0.45f), radius = w * 0.012f, center = Offset(x, h * 0.31f))
        x += w * 0.06f
    }
    listOf(0.49f, 0.66f, 0.83f).forEach { yF ->
        drawLine(Color(0xFFD1D1D6).copy(alpha = 0.75f), Offset(0f, h * yF), Offset(w, h * yF), strokeWidth = h * 0.018f)
    }
}

private fun DrawScope.drawRemindersArtwork(darkThemeMode: Boolean) {
    drawRect(if (darkThemeMode) Color(0xFF1C1C1E) else Color(0xFFFAFAFC))
    val w = size.width
    val h = size.height
    val items = listOf(0.27f to Color(0xFF007AFF), 0.50f to Color(0xFFFF3B30), 0.73f to Color(0xFFFF9500))
    items.forEach { (yF, color) ->
        drawCircle(color.copy(alpha = 0.18f), radius = w * 0.088f, center = Offset(w * 0.22f, h * yF))
        drawCircle(color, radius = w * 0.062f, center = Offset(w * 0.22f, h * yF))
        drawLine(Color(0xFFD1D1D6), Offset(w * 0.36f, h * yF), Offset(w * 0.83f, h * yF), strokeWidth = h * 0.024f, cap = StrokeCap.Round)
    }
}

private fun DrawScope.drawTvArtwork(textMeasurer: TextMeasurer) {
    drawRect(brush = Brush.verticalGradient(listOf(Color(0xFF28282B), Color(0xFF0C0C0E))))
    val w = size.width
    val h = size.height
    val iconDp = w / density
    val label = "tv"
    val style = TextStyle(
        color = Color.White,
        fontFamily = IosSansFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = (iconDp * 0.36f).sp,
        letterSpacing = (-0.8).sp
    )
    val measured = textMeasurer.measure(label, style)
    val startX = w * 0.59f - measured.size.width / 2f
    val startY = h * 0.50f - measured.size.height / 2f
    drawText(measured, topLeft = Offset(startX, startY))

    val cx = startX - w * 0.115f
    val cy = h * 0.525f
    drawCircle(Color.White, radius = w * 0.082f, center = Offset(cx, cy))
    drawCircle(Color(0xFF19191C), radius = w * 0.036f, center = Offset(cx + w * 0.056f, cy - h * 0.02f))
    drawOval(Color.White, topLeft = Offset(cx - w * 0.01f, cy - w * 0.145f), size = Size(w * 0.045f, w * 0.055f))
}

private fun DrawScope.drawFilesArtwork(darkThemeMode: Boolean) {
    drawRect(if (darkThemeMode) Color(0xFF1C1C1E) else Color.White)
    val w = size.width
    val h = size.height
    val tab = Path().apply {
        moveTo(w * 0.15f, h * 0.24f)
        lineTo(w * 0.40f, h * 0.24f)
        lineTo(w * 0.47f, h * 0.31f)
        lineTo(w * 0.85f, h * 0.31f)
        lineTo(w * 0.85f, h * 0.42f)
        lineTo(w * 0.15f, h * 0.42f)
        close()
    }
    drawPath(tab, Color(0xFF1985E2))
    drawRoundRect(
        brush = Brush.verticalGradient(listOf(Color(0xFF66D1FF), Color(0xFF1A8EEA))),
        topLeft = Offset(w * 0.15f, h * 0.33f),
        size = Size(w * 0.70f, h * 0.45f),
        cornerRadius = CornerRadius(w * 0.085f, w * 0.085f)
    )
}

private fun DrawScope.drawMailArtwork() {
    drawRect(brush = Brush.verticalGradient(listOf(Color(0xFF289CFF), Color(0xFF0055D4))))
    val w = size.width
    val h = size.height
    drawRoundRect(
        color = Color.White,
        topLeft = Offset(w * 0.15f, h * 0.29f),
        size = Size(w * 0.70f, h * 0.42f),
        cornerRadius = CornerRadius(w * 0.065f, w * 0.065f)
    )
    val flap = Path().apply {
        moveTo(w * 0.16f, h * 0.31f)
        lineTo(w * 0.50f, h * 0.54f)
        lineTo(w * 0.84f, h * 0.31f)
    }
    drawPath(flap, Color(0xFF1D7AE8), style = Stroke(width = w * 0.032f, cap = StrokeCap.Round, join = StrokeJoin.Round))
}

private fun DrawScope.drawContactsArtwork() {
    drawRect(Color(0xFFECEBE6))
    val w = size.width
    val h = size.height
    val tabs = listOf(Color(0xFF34AADC), Color(0xFF4CD964), Color(0xFFFF9500), Color(0xFFFF3B30))
    tabs.forEachIndexed { idx, c ->
        drawRect(c, topLeft = Offset(w * 0.84f, h * (0.14f + idx * 0.18f)), size = Size(w * 0.16f, h * 0.17f))
    }
    drawCircle(Color(0xFF8E8E93), radius = w * 0.13f, center = Offset(w * 0.44f, h * 0.37f))
    drawArc(
        color = Color(0xFF8E8E93),
        startAngle = 180f,
        sweepAngle = 180f,
        useCenter = true,
        topLeft = Offset(w * 0.22f, h * 0.51f),
        size = Size(w * 0.44f, h * 0.33f)
    )
}

private fun DrawScope.drawCalculatorArtwork() {
    drawRect(Color(0xFF1C1C1E))
    val w = size.width
    val h = size.height
    drawRoundRect(Color(0xFF2C2C2E), Offset(w * 0.15f, h * 0.13f), Size(w * 0.70f, h * 0.17f), CornerRadius(w * 0.04f, w * 0.04f))
    val cols = listOf(0.24f, 0.415f, 0.59f, 0.765f)
    val rows = listOf(0.41f, 0.56f, 0.71f, 0.86f)
    rows.forEachIndexed { rIdx, yF ->
        if (rIdx == 3) {
            // Wide 0 pill
            drawRoundRect(
                color = Color(0xFF505050),
                topLeft = Offset(w * 0.17f, h * (yF - 0.068f)),
                size = Size(w * 0.315f, w * 0.136f),
                cornerRadius = CornerRadius(w * 0.068f, w * 0.068f)
            )
            drawCircle(Color(0xFF505050), radius = w * 0.068f, center = Offset(w * cols[2], h * yF))
            drawCircle(Color(0xFFFF9F0A), radius = w * 0.068f, center = Offset(w * cols[3], h * yF))
        } else {
            cols.forEachIndexed { cIdx, xF ->
                val color = when {
                    cIdx == 3 -> Color(0xFFFF9F0A)
                    rIdx == 0 -> Color(0xFFA5A5A5)
                    else -> Color(0xFF505050)
                }
                drawCircle(color, radius = w * 0.068f, center = Offset(w * xF, h * yF))
            }
        }
    }
}

private fun DrawScope.drawGoogleArtwork(darkThemeMode: Boolean) {
    drawRect(if (darkThemeMode) Color(0xFF1C1C1E) else Color.White)
    val w = size.width
    val center = Offset(w / 2f, w / 2f)
    val r = w * 0.28f
    val stroke = w * 0.125f
    val topLeft = Offset(center.x - r, center.y - r)
    val box = Size(r * 2f, r * 2f)
    drawArc(Color(0xFFEA4335), 215f, 105f, false, topLeft, box, style = Stroke(stroke))
    drawArc(Color(0xFFFBBC05), 145f, 70f, false, topLeft, box, style = Stroke(stroke))
    drawArc(Color(0xFF34A853), 40f, 105f, false, topLeft, box, style = Stroke(stroke))
    drawArc(Color(0xFF4285F4), -6f, 52f, false, topLeft, box, style = Stroke(stroke))
    drawLine(Color(0xFF4285F4), Offset(center.x, center.y), Offset(center.x + r + stroke * 0.35f, center.y), strokeWidth = stroke * 0.85f)
}

private fun DrawScope.drawYouTubeArtwork(darkThemeMode: Boolean) {
    drawRect(if (darkThemeMode) Color(0xFF1C1C1E) else Color.White)
    val w = size.width
    val h = size.height
    drawRoundRect(
        color = Color(0xFFFF0000),
        topLeft = Offset(w * 0.12f, h * 0.25f),
        size = Size(w * 0.76f, h * 0.50f),
        cornerRadius = CornerRadius(w * 0.15f, w * 0.15f)
    )
    val play = Path().apply {
        moveTo(w * 0.42f, h * 0.38f)
        lineTo(w * 0.65f, h * 0.50f)
        lineTo(w * 0.42f, h * 0.62f)
        close()
    }
    drawPath(play, Color.White)
}

private fun DrawScope.drawWhatsAppArtwork() {
    drawRect(brush = Brush.verticalGradient(listOf(Color(0xFF66F27E), Color(0xFF25D366))))
    val w = size.width
    val h = size.height
    val center = Offset(w * 0.50f, h * 0.48f)
    // Outer white ring + tail
    drawCircle(Color.White, radius = w * 0.285f, center = center, style = Stroke(width = w * 0.068f))
    val tail = Path().apply {
        moveTo(w * 0.27f, h * 0.62f)
        lineTo(w * 0.21f, h * 0.78f)
        lineTo(w * 0.38f, h * 0.72f)
        close()
    }
    drawPath(tail, Color.White)
    // Clean inner green fill over tail seam + white phone handset inside
    drawCircle(Color(0xFF3FE070), radius = w * 0.25f, center = center)
    val innerHandset = Path().apply {
        moveTo(w * 0.38f, h * 0.35f)
        cubicTo(w * 0.41f, h * 0.34f, w * 0.45f, h * 0.37f, w * 0.46f, h * 0.41f)
        cubicTo(w * 0.46f, h * 0.44f, w * 0.44f, h * 0.45f, w * 0.45f, h * 0.48f)
        cubicTo(w * 0.47f, h * 0.51f, w * 0.50f, h * 0.54f, w * 0.53f, h * 0.55f)
        cubicTo(w * 0.55f, h * 0.54f, w * 0.57f, h * 0.52f, w * 0.60f, h * 0.54f)
        cubicTo(w * 0.63f, h * 0.56f, w * 0.65f, h * 0.60f, w * 0.63f, h * 0.63f)
        cubicTo(w * 0.59f, h * 0.67f, w * 0.51f, h * 0.66f, w * 0.44f, h * 0.59f)
        cubicTo(w * 0.37f, h * 0.52f, w * 0.34f, h * 0.43f, w * 0.38f, h * 0.35f)
        close()
    }
    drawPath(innerHandset, Color.White)
}

private fun DrawScope.drawInstagramArtwork() {
    drawRect(
        brush = Brush.linearGradient(
            colors = listOf(Color(0xFF4F5BD5), Color(0xFFD62976), Color(0xFFFA7E1E), Color(0xFFFEDA75)),
            start = Offset(0f, 0f),
            end = Offset(size.width, size.height)
        )
    )
    val w = size.width
    drawRoundRect(
        color = Color.White,
        topLeft = Offset(w * 0.18f, w * 0.18f),
        size = Size(w * 0.64f, w * 0.64f),
        cornerRadius = CornerRadius(w * 0.18f, w * 0.18f),
        style = Stroke(width = w * 0.062f)
    )
    drawCircle(Color.White, radius = w * 0.155f, center = Offset(w * 0.50f, w * 0.50f), style = Stroke(width = w * 0.062f))
    drawCircle(Color.White, radius = w * 0.044f, center = Offset(w * 0.675f, w * 0.325f))
}

private fun DrawScope.drawTikTokArtwork() {
    drawRect(Color(0xFF0A0A0D))
    val w = size.width
    val h = size.height
    fun drawTikTokGlyph(dx: Float, dy: Float, color: Color) {
        val path = Path().apply {
            moveTo(w * 0.54f + dx, h * 0.22f + dy)
            lineTo(w * 0.63f + dx, h * 0.22f + dy)
            cubicTo(w * 0.64f + dx, h * 0.33f + dy, w * 0.71f + dx, h * 0.39f + dy, w * 0.80f + dx, h * 0.40f + dy)
            lineTo(w * 0.80f + dx, h * 0.49f + dy)
            cubicTo(w * 0.72f + dx, h * 0.49f + dy, w * 0.66f + dx, h * 0.46f + dy, w * 0.63f + dx, h * 0.42f + dy)
            lineTo(w * 0.63f + dx, h * 0.62f + dy)
            cubicTo(w * 0.63f + dx, h * 0.74f + dy, w * 0.53f + dx, h * 0.81f + dy, w * 0.41f + dx, h * 0.81f + dy)
            cubicTo(w * 0.29f + dx, h * 0.81f + dy, w * 0.22f + dx, h * 0.72f + dy, w * 0.22f + dx, h * 0.62f + dy)
            cubicTo(w * 0.22f + dx, h * 0.50f + dy, w * 0.32f + dx, h * 0.42f + dy, w * 0.44f + dx, h * 0.44f + dy)
            lineTo(w * 0.44f + dx, h * 0.53f + dy)
            cubicTo(w * 0.38f + dx, h * 0.52f + dy, w * 0.32f + dx, h * 0.56f + dy, w * 0.32f + dx, h * 0.62f + dy)
            cubicTo(w * 0.32f + dx, h * 0.68f + dy, w * 0.37f + dx, h * 0.71f + dy, w * 0.43f + dx, h * 0.71f + dy)
            cubicTo(w * 0.49f + dx, h * 0.71f + dy, w * 0.54f + dx, h * 0.67f + dy, w * 0.54f + dx, h * 0.60f + dy)
            close()
        }
        drawPath(path, color)
    }
    drawTikTokGlyph(-w * 0.024f, -h * 0.02f, Color(0xFF25F4EE))
    drawTikTokGlyph(w * 0.024f, h * 0.02f, Color(0xFFFE2C55))
    drawTikTokGlyph(0f, 0f, Color.White)
}

private fun DrawScope.drawChromeArtwork(darkThemeMode: Boolean) {
    drawRect(if (darkThemeMode) Color(0xFF1C1C1E) else Color.White)
    val w = size.width
    val center = Offset(w / 2f, w / 2f)
    val r = w * 0.36f
    drawArc(Color(0xFFEA4335), 205f, 130f, true, Offset(center.x - r, center.y - r), Size(r * 2, r * 2))
    drawArc(Color(0xFFFBBC05), 335f, 120f, true, Offset(center.x - r, center.y - r), Size(r * 2, r * 2))
    drawArc(Color(0xFF34A853), 95f, 110f, true, Offset(center.x - r, center.y - r), Size(r * 2, r * 2))
    drawCircle(Color.White, radius = w * 0.185f, center = center)
    drawCircle(Color(0xFF4285F4), radius = w * 0.142f, center = center)
}

private fun DrawScope.drawHomeArtwork(darkThemeMode: Boolean) {
    drawRect(if (darkThemeMode) Color(0xFF1C1C1E) else Color.White)
    val w = size.width
    val h = size.height
    val house = Path().apply {
        moveTo(w * 0.50f, h * 0.19f)
        lineTo(w * 0.83f, h * 0.45f)
        lineTo(w * 0.75f, h * 0.45f)
        lineTo(w * 0.75f, h * 0.79f)
        lineTo(w * 0.25f, h * 0.79f)
        lineTo(w * 0.25f, h * 0.45f)
        lineTo(w * 0.17f, h * 0.45f)
        close()
    }
    drawPath(house, Brush.verticalGradient(listOf(Color(0xFFFFB53E), Color(0xFFFF7A00))))
    drawRoundRect(Color(0xFFFFE5B4), Offset(w * 0.36f, h * 0.47f), Size(w * 0.28f, h * 0.24f), CornerRadius(w * 0.05f, w * 0.05f))
}

private fun DrawScope.drawSpotifyArtwork() {
    drawRect(Color(0xFF1DB954))
    val w = size.width
    listOf(0.38f to 0.52f, 0.51f to 0.44f, 0.64f to 0.36f).forEach { (yF, spanW) ->
        drawArc(
            color = Color(0xFF121212),
            startAngle = 225f,
            sweepAngle = 90f,
            useCenter = false,
            topLeft = Offset(w * (0.5f - spanW / 2f), w * yF),
            size = Size(w * spanW, w * 0.26f),
            style = Stroke(width = w * 0.065f, cap = StrokeCap.Round)
        )
    }
}

private fun DrawScope.drawTelegramArtwork() {
    drawRect(brush = Brush.verticalGradient(listOf(Color(0xFF37AEE2), Color(0xFF1E96C8))))
    val w = size.width
    val h = size.height
    val plane = Path().apply {
        moveTo(w * 0.20f, h * 0.49f)
        lineTo(w * 0.78f, h * 0.26f)
        lineTo(w * 0.66f, h * 0.75f)
        lineTo(w * 0.46f, h * 0.59f)
        lineTo(w * 0.66f, h * 0.37f)
        lineTo(w * 0.37f, h * 0.54f)
        close()
    }
    drawPath(plane, Color.White)
}

private fun DrawScope.drawFacebookArtwork() {
    drawRect(brush = Brush.verticalGradient(listOf(Color(0xFF18ACFE), Color(0xFF0163E0))))
    val w = size.width
    val h = size.height
    drawLine(Color.White, Offset(w * 0.54f, h * 0.34f), Offset(w * 0.54f, h * 0.86f), strokeWidth = w * 0.13f)
    drawArc(
        color = Color.White,
        startAngle = 180f,
        sweepAngle = 90f,
        useCenter = false,
        topLeft = Offset(w * 0.54f, h * 0.24f),
        size = Size(w * 0.26f, h * 0.26f),
        style = Stroke(width = w * 0.13f, cap = StrokeCap.Round)
    )
    drawLine(Color.White, Offset(w * 0.40f, h * 0.52f), Offset(w * 0.69f, h * 0.52f), strokeWidth = w * 0.11f)
}

private fun DrawScope.drawXArtwork() {
    drawRect(Color(0xFF0B0B0D))
    val w = size.width
    drawLine(Color.White, Offset(w * 0.26f, w * 0.24f), Offset(w * 0.74f, w * 0.76f), strokeWidth = w * 0.09f, cap = StrokeCap.Round)
    drawLine(Color.White, Offset(w * 0.74f, w * 0.24f), Offset(w * 0.26f, w * 0.76f), strokeWidth = w * 0.07f, cap = StrokeCap.Round)
}
