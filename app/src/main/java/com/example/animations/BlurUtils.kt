package com.example.animations

import android.os.Build
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.compatibleBlur(
    radius: Dp,
    lowEndGpuFallback: Boolean = false
): Modifier {
    if (radius <= 0.5.dp || lowEndGpuFallback) return this
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        this.blur(radius = radius, edgeTreatment = BlurredEdgeTreatment.Rectangle)
    } else {
        this
    }
}
