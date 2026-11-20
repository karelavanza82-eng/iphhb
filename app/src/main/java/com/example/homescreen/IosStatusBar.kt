package com.example.homescreen

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.BatteryManager
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.IosSansFontFamily
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun IosStatusBar(
    isLocationActive: Boolean,
    contentColor: Color = Color.White,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var timeText by remember { mutableStateOf("") }
    var batteryPct by remember { mutableIntStateOf(100) }
    var isCharging by remember { mutableStateOf(false) }
    var hasWifi by remember { mutableStateOf(true) }
    var hasCellular by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val fmt = SimpleDateFormat("h:mm", Locale.getDefault())
        while (true) {
            timeText = fmt.format(Date())
            val status = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            if (status != null) {
                val lvl = status.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale = status.getIntExtra(BatteryManager.EXTRA_SCALE, 100)
                if (lvl >= 0 && scale > 0) batteryPct = (lvl * 100) / scale
                isCharging = status.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0) != 0
            }
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            val caps = cm?.getNetworkCapabilities(cm.activeNetwork)
            hasWifi = caps?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true
            hasCellular = caps?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true
            delay(10_000L)
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = timeText,
                color = contentColor,
                fontFamily = IosSansFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp
            )
            if (isLocationActive) {
                Spacer(Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.NearMe,
                    contentDescription = "Location active",
                    tint = contentColor,
                    modifier = Modifier.size(12.dp)
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Cellular Signal Staircase Bars
            Canvas(modifier = Modifier.size(width = 17.dp, height = 11.dp)) {
                val barW = size.width / 5.5f
                val gap = barW * 0.45f
                val heights = listOf(0.32f, 0.52f, 0.74f, 1.0f)
                heights.forEachIndexed { idx, frac ->
                    val active = hasCellular || idx < 2
                    val barH = size.height * frac
                    drawRoundRect(
                        color = if (active) contentColor else contentColor.copy(alpha = 0.35f),
                        topLeft = Offset(idx * (barW + gap), size.height - barH),
                        size = Size(barW, barH),
                        cornerRadius = CornerRadius(1.5.dp.toPx(), 1.5.dp.toPx())
                    )
                }
            }

            if (hasWifi) {
                Icon(
                    imageVector = Icons.Default.Wifi,
                    contentDescription = "Wi-Fi",
                    tint = contentColor,
                    modifier = Modifier.size(16.dp)
                )
            }

            // Authentic iOS Battery Icon
            Canvas(modifier = Modifier.size(width = 25.dp, height = 12.dp)) {
                val bodyW = size.width - 3.dp.toPx()
                val bodyH = size.height
                drawRoundRect(
                    color = contentColor.copy(alpha = 0.45f),
                    topLeft = Offset.Zero,
                    size = Size(bodyW, bodyH),
                    cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx()),
                    style = Stroke(width = 1.1.dp.toPx())
                )
                val innerPad = 1.8.dp.toPx()
                val maxFillW = (bodyW - innerPad * 2f).coerceAtLeast(1f)
                val fillW = maxFillW * (batteryPct / 100f).coerceIn(0.08f, 1f)
                val fillColor = when {
                    isCharging -> Color(0xFF34C759)
                    batteryPct <= 20 -> Color(0xFFFF3B30)
                    else -> contentColor
                }
                drawRoundRect(
                    color = fillColor,
                    topLeft = Offset(innerPad, innerPad),
                    size = Size(fillW, bodyH - innerPad * 2f),
                    cornerRadius = CornerRadius(1.6.dp.toPx(), 1.6.dp.toPx())
                )
                // Positive battery cap
                drawRoundRect(
                    color = contentColor.copy(alpha = 0.5f),
                    topLeft = Offset(bodyW + 1.dp.toPx(), bodyH * 0.30f),
                    size = Size(1.8.dp.toPx(), bodyH * 0.40f),
                    cornerRadius = CornerRadius(1.dp.toPx(), 1.dp.toPx())
                )
            }
        }
    }
}
