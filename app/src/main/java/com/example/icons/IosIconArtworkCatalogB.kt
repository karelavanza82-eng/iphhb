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

@Composable
fun DrawIosIconCatalogB(
    key: IosIconKey,
    darkThemeMode: Boolean,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        when (key) {
            IosIconKey.WALLET -> drawWalletArtwork()
            IosIconKey.NOTES -> drawNotesArtwork(darkThemeMode)
            IosIconKey.REMINDERS -> drawRemindersArtwork(darkThemeMode)
            IosIconKey.TV -> drawTvArtwork()
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
    drawRoundRect(Color(0xFF2C2C2E), Offset(w * 0.14f, h * 0.20f), Size(w * 0.72f, h * 0.62f), CornerRadius(w * 0.10f, w * 0.10f))
    drawRoundRect(Color(0xFF34AADC), Offset(w * 0.18f, h * 0.25f), Size(w * 0.64f, h * 0.20f), CornerRadius(w * 0.05f, w * 0.05f))
    drawRoundRect(Color(0xFFFFCC00), Offset(w * 0.18f, h * 0.31f), Size(w * 0.64f, h * 0.20f), CornerRadius(w * 0.05f, w * 0.05f))
    drawRoundRect(Color(0xFF4CD964), Offset(w * 0.18f, h * 0.37f), Size(w * 0.64f, h * 0.20f), CornerRadius(w * 0.05f, w * 0.05f))
    drawRoundRect(Color(0xFFFF6951), Offset(w * 0.18f, h * 0.43f), Size(w * 0.64f, h * 0.20f), CornerRadius(w * 0.05f, w * 0.05f))
    drawRoundRect(Color(0xFFD6CEC2), Offset(w * 0.14f, h * 0.48f), Size(w * 0.72f, h * 0.34f), CornerRadius(w * 0.09f, w * 0.09f))
    drawCircle(Color(0xFF2C2C2E), radius = w * 0.10f, center = Offset(w * 0.50f, h * 0.48f))
}

private fun DrawScope.drawNotesArtwork(darkThemeMode: Boolean) {
    drawRect(if (darkThemeMode) Color(0xFF1C1C1E) else Color(0xFFFCFCFE))
    val w = size.width
    val h = size.height
    drawRect(
        brush = Brush.verticalGradient(listOf(Color(0xFFFFD83B), Color(0xFFFFC300))),
        size = Size(w, h * 0.28f)
    )
    drawLine(Color(0xFFD1D1D6), Offset(0f, h * 0.30f), Offset(w, h * 0.30f), strokeWidth = h * 0.02f)
    listOf(0.47f, 0.64f, 0.81f).forEach { yFrac ->
        drawLine(Color(0xFFD1D1D6).copy(alpha = 0.7f), Offset(0f, h * yFrac), Offset(w, h * yFrac), strokeWidth = h * 0.018f)
    }
}

private fun DrawScope.drawRemindersArtwork(darkThemeMode: Boolean) {
    drawRect(if (darkThemeMode) Color(0xFF1C1C1E) else Color(0xFFFAFAFC))
    val w = size.width
    val h = size.height
    val items = listOf(0.28f to Color(0xFF007AFF), 0.50f to Color(0xFFFF3B30), 0.72f to Color(0xFFFF9500))
    items.forEach { (yF, color) ->
        drawCircle(color, radius = w * 0.068f, center = Offset(w * 0.22f, h * yF))
        drawLine(Color(0xFFD1D1D6), Offset(w * 0.35f, h * yF), Offset(w * 0.82f, h * yF), strokeWidth = h * 0.028f, cap = StrokeCap.Round)
    }
}

private fun DrawScope.drawTvArtwork() {
    drawRect(brush = Brush.verticalGradient(listOf(Color(0xFF232327), Color(0xFF0D0D0F))))
    val w = size.width
    val h = size.height
    drawCircle(Color.White, radius = w * 0.085f, center = Offset(w * 0.28f, h * 0.52f))
    drawCircle(Color(0xFF0D0D0F), radius = w * 0.035f, center = Offset(w * 0.34f, h * 0.50f))
    drawLine(Color.White, Offset(w * 0.48f, h * 0.38f), Offset(w * 0.48f, h * 0.63f), strokeWidth = w * 0.06f, cap = StrokeCap.Round)
    drawLine(Color.White, Offset(w * 0.43f, h * 0.44f), Offset(w * 0.54f, h * 0.44f), strokeWidth = w * 0.05f, cap = StrokeCap.Round)
    val vPath = Path().apply {
        moveTo(w * 0.60f, h * 0.44f)
        lineTo(w * 0.69f, h * 0.62f)
        lineTo(w * 0.78f, h * 0.44f)
    }
    drawPath(vPath, Color.White, style = Stroke(width = w * 0.055f, cap = StrokeCap.Round, join = StrokeJoin.Round))
}

private fun DrawScope.drawFilesArtwork(darkThemeMode: Boolean) {
    drawRect(if (darkThemeMode) Color(0xFF1C1C1E) else Color.White)
    val w = size.width
    val h = size.height
    val tab = Path().apply {
        moveTo(w * 0.16f, h * 0.25f)
        lineTo(w * 0.40f, h * 0.25f)
        lineTo(w * 0.46f, h * 0.32f)
        lineTo(w * 0.84f, h * 0.32f)
        lineTo(w * 0.84f, h * 0.42f)
        lineTo(w * 0.16f, h * 0.42f)
        close()
    }
    drawPath(tab, Color(0xFF1F8AE6))
    drawRoundRect(
        brush = Brush.verticalGradient(listOf(Color(0xFF63D0FF), Color(0xFF2196F3))),
        topLeft = Offset(w * 0.16f, h * 0.34f),
        size = Size(w * 0.68f, h * 0.44f),
        cornerRadius = CornerRadius(w * 0.08f, w * 0.08f)
    )
}

private fun DrawScope.drawMailArtwork() {
    drawRect(brush = Brush.verticalGradient(listOf(Color(0xFF2C9AFF), Color(0xFF0056D6))))
    val w = size.width
    val h = size.height
    drawRoundRect(
        color = Color.White,
        topLeft = Offset(w * 0.16f, h * 0.29f),
        size = Size(w * 0.68f, h * 0.42f),
        cornerRadius = CornerRadius(w * 0.06f, w * 0.06f)
    )
    val flap = Path().apply {
        moveTo(w * 0.17f, h * 0.31f)
        lineTo(w * 0.50f, h * 0.53f)
        lineTo(w * 0.83f, h * 0.31f)
    }
    drawPath(flap, Color(0xFF2B7CEB), style = Stroke(width = w * 0.032f, cap = StrokeCap.Round, join = StrokeJoin.Round))
}

private fun DrawScope.drawContactsArtwork() {
    drawRect(Color(0xFFECEBE6))
    val w = size.width
    val h = size.height
    val tabs = listOf(Color(0xFF007AFF), Color(0xFF34C759), Color(0xFFFF9500), Color(0xFFFF3B30))
    tabs.forEachIndexed { idx, c ->
        drawRect(c, topLeft = Offset(w * 0.84f, h * (0.14f + idx * 0.18f)), size = Size(w * 0.16f, h * 0.17f))
    }
    drawCircle(Color(0xFF8E8E93), radius = w * 0.13f, center = Offset(w * 0.44f, h * 0.38f))
    drawArc(
        color = Color(0xFF8E8E93),
        startAngle = 180f,
        sweepAngle = 180f,
        useCenter = true,
        topLeft = Offset(w * 0.22f, h * 0.52f),
        size = Size(w * 0.44f, h * 0.32f)
    )
}

private fun DrawScope.drawCalculatorArtwork() {
    drawRect(Color(0xFF1C1C1E))
    val w = size.width
    val h = size.height
    drawRoundRect(Color(0xFF2C2C2E), Offset(w * 0.16f, h * 0.14f), Size(w * 0.68f, h * 0.17f), CornerRadius(w * 0.04f, w * 0.04f))
    val cols = listOf(0.24f, 0.42f, 0.60f, 0.77f)
    val rows = listOf(0.42f, 0.57f, 0.72f, 0.86f)
    rows.forEachIndexed { rIdx, yF ->
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

private fun DrawScope.drawGoogleArtwork(darkThemeMode: Boolean) {
    drawRect(if (darkThemeMode) Color(0xFF1C1C1E) else Color.White)
    val w = size.width
    val center = Offset(w / 2f, w / 2f)
    val r = w * 0.28f
    val stroke = w * 0.12f
    val topLeft = Offset(center.x - r, center.y - r)
    val box = Size(r * 2f, r * 2f)
    drawArc(Color(0xFFEA4335), 215f, 105f, false, topLeft, box, style = Stroke(stroke))
    drawArc(Color(0xFFFBBC05), 145f, 70f, false, topLeft, box, style = Stroke(stroke))
    drawArc(Color(0xFF34A853), 40f, 105f, false, topLeft, box, style = Stroke(stroke))
    drawArc(Color(0xFF4285F4), -5f, 55f, false, topLeft, box, style = Stroke(stroke))
    drawLine(Color(0xFF4285F4), Offset(center.x, center.y), Offset(center.x + r + stroke * 0.35f, center.y), strokeWidth = stroke * 0.85f)
}

private fun DrawScope.drawYouTubeArtwork(darkThemeMode: Boolean) {
    drawRect(if (darkThemeMode) Color(0xFF1C1C1E) else Color.White)
    val w = size.width
    val h = size.height
    drawRoundRect(
        color = Color(0xFFFF0000),
        topLeft = Offset(w * 0.13f, h * 0.26f),
        size = Size(w * 0.74f, h * 0.48f),
        cornerRadius = CornerRadius(w * 0.14f, w * 0.14f)
    )
    val play = Path().apply {
        moveTo(w * 0.43f, h * 0.39f)
        lineTo(w * 0.64f, h * 0.50f)
        lineTo(w * 0.43f, h * 0.61f)
        close()
    }
    drawPath(play, Color.White)
}

private fun DrawScope.drawWhatsAppArtwork() {
    drawRect(brush = Brush.verticalGradient(listOf(Color(0xFF63F07A), Color(0xFF25D366))))
    val w = size.width
    val center = Offset(w * 0.50f, w * 0.48f)
    drawCircle(Color.White, radius = w * 0.28f, center = center, style = Stroke(width = w * 0.065f))
    val tail = Path().apply {
        moveTo(w * 0.28f, w * 0.64f)
        lineTo(w * 0.22f, w * 0.78f)
        lineTo(w * 0.38f, w * 0.72f)
        close()
    }
    drawPath(tail, Color.White)
    drawArc(
        color = Color.White,
        startAngle = 25f,
        sweepAngle = 155f,
        useCenter = false,
        topLeft = Offset(w * 0.35f, w * 0.35f),
        size = Size(w * 0.28f, w * 0.28f),
        style = Stroke(width = w * 0.055f, cap = StrokeCap.Round)
    )
}

private fun DrawScope.drawInstagramArtwork() {
    drawRect(
        brush = Brush.linearGradient(
            colors = listOf(Color(0xFF515BD4), Color(0xFFDD2A7B), Color(0xFFF58529), Color(0xFFFEDA77)),
            start = Offset(0f, 0f),
            end = Offset(size.width, size.height)
        )
    )
    val w = size.width
    drawRoundRect(
        color = Color.White,
        topLeft = Offset(w * 0.19f, w * 0.19f),
        size = Size(w * 0.62f, w * 0.62f),
        cornerRadius = CornerRadius(w * 0.17f, w * 0.17f),
        style = Stroke(width = w * 0.06f)
    )
    drawCircle(Color.White, radius = w * 0.15f, center = Offset(w * 0.50f, w * 0.50f), style = Stroke(width = w * 0.06f))
    drawCircle(Color.White, radius = w * 0.042f, center = Offset(w * 0.67f, w * 0.33f))
}

private fun DrawScope.drawTikTokArtwork() {
    drawRect(Color(0xFF0A0A0C))
    val w = size.width
    val h = size.height
    fun drawNote(dx: Float, dy: Float, color: Color) {
        drawLine(color, Offset(w * 0.56f + dx, h * 0.24f + dy), Offset(w * 0.56f + dx, h * 0.62f + dy), strokeWidth = w * 0.09f, cap = StrokeCap.Round)
        drawArc(
            color = color,
            startAngle = 90f,
            sweepAngle = 90f,
            useCenter = false,
            topLeft = Offset(w * 0.56f + dx, h * 0.12f + dy),
            size = Size(w * 0.32f, h * 0.26f),
            style = Stroke(width = w * 0.085f, cap = StrokeCap.Round)
        )
        drawCircle(color, radius = w * 0.145f, center = Offset(w * 0.44f + dx, h * 0.63f + dy), style = Stroke(width = w * 0.085f))
    }
    drawNote(-w * 0.025f, -h * 0.02f, Color(0xFF25F4EE))
    drawNote(w * 0.025f, h * 0.02f, Color(0xFFFE2C55))
    drawNote(0f, 0f, Color.White)
}

private fun DrawScope.drawChromeArtwork(darkThemeMode: Boolean) {
    drawRect(if (darkThemeMode) Color(0xFF1C1C1E) else Color.White)
    val w = size.width
    val center = Offset(w / 2f, w / 2f)
    val r = w * 0.36f
    drawArc(Color(0xFFEA4335), 205f, 130f, true, Offset(center.x - r, center.y - r), Size(r * 2, r * 2))
    drawArc(Color(0xFFFBBC05), 335f, 120f, true, Offset(center.x - r, center.y - r), Size(r * 2, r * 2))
    drawArc(Color(0xFF34A853), 95f, 110f, true, Offset(center.x - r, center.y - r), Size(r * 2, r * 2))
    drawCircle(Color.White, radius = w * 0.18f, center = center)
    drawCircle(Color(0xFF4285F4), radius = w * 0.14f, center = center)
}

private fun DrawScope.drawHomeArtwork(darkThemeMode: Boolean) {
    drawRect(if (darkThemeMode) Color(0xFF1C1C1E) else Color.White)
    val w = size.width
    val h = size.height
    val house = Path().apply {
        moveTo(w * 0.50f, h * 0.20f)
        lineTo(w * 0.82f, h * 0.45f)
        lineTo(w * 0.74f, h * 0.45f)
        lineTo(w * 0.74f, h * 0.78f)
        lineTo(w * 0.26f, h * 0.78f)
        lineTo(w * 0.26f, h * 0.45f)
        lineTo(w * 0.18f, h * 0.45f)
        close()
    }
    drawPath(house, Brush.verticalGradient(listOf(Color(0xFFFFB340), Color(0xFFFF7B00))))
    drawRoundRect(Color(0xFFFFE5B4), Offset(w * 0.37f, h * 0.48f), Size(w * 0.26f, h * 0.22f), CornerRadius(w * 0.04f, w * 0.04f))
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
