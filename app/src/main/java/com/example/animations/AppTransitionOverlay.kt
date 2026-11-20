package com.example.animations

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.example.database.HomeItemEntity
import com.example.database.LauncherSettings
import com.example.icons.IosAppIcon
import kotlin.math.roundToInt

sealed interface AppTransitionPhase {
    data object Idle : AppTransitionPhase
    data class Opening(
        val item: HomeItemEntity,
        val sourceBounds: Rect,
        val isInternalTarget: Boolean
    ) : AppTransitionPhase

    data class Closing(
        val item: HomeItemEntity,
        val targetBounds: Rect
    ) : AppTransitionPhase
}

@Composable
fun AppTransitionOverlay(
    phase: AppTransitionPhase,
    settings: LauncherSettings,
    onProgressChanged: (Float) -> Unit,
    onOpeningReadyToLaunch: (HomeItemEntity) -> Unit,
    onTransitionFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val containerSize = remember { androidx.compose.runtime.mutableStateOf(IntSize.Zero) }
    val progress = remember { Animatable(0f) }

    LaunchedEffect(phase) {
        when (phase) {
            AppTransitionPhase.Idle -> {
                progress.snapTo(0f)
                onProgressChanged(0f)
            }
            is AppTransitionPhase.Opening -> {
                if (!settings.animationsEnabled || !settings.appOpenAnimationEnabled) {
                    onProgressChanged(0f)
                    onOpeningReadyToLaunch(phase.item)
                    onTransitionFinished()
                    return@LaunchedEffect
                }
                progress.snapTo(0f)
                val stiffness = settings.springIntensity.stiffness / settings.animationSpeed.durationScale
                val damping = settings.springIntensity.dampingRatio
                var launched = false
                progress.animateTo(
                    targetValue = 1f,
                    animationSpec = spring(
                        dampingRatio = damping,
                        stiffness = stiffness
                    )
                ) {
                    onProgressChanged(value)
                    if (!launched && value >= 0.78f) {
                        launched = true
                        onOpeningReadyToLaunch(phase.item)
                    }
                }
                if (!launched) {
                    onOpeningReadyToLaunch(phase.item)
                }
                onTransitionFinished()
            }
            is AppTransitionPhase.Closing -> {
                if (!settings.animationsEnabled || !settings.appCloseAnimationEnabled) {
                    onProgressChanged(0f)
                    onTransitionFinished()
                    return@LaunchedEffect
                }
                progress.snapTo(1f)
                onProgressChanged(1f)
                val durationMs = (280 * settings.animationSpeed.durationScale).roundToInt().coerceIn(140, 480)
                progress.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(durationMillis = durationMs)
                ) {
                    onProgressChanged(value)
                }
                onProgressChanged(0f)
                onTransitionFinished()
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged { containerSize.value = it }
    ) {
        val bounds = when (phase) {
            AppTransitionPhase.Idle -> null
            is AppTransitionPhase.Opening -> phase.sourceBounds
            is AppTransitionPhase.Closing -> phase.targetBounds
        }
        val item = when (phase) {
            AppTransitionPhase.Idle -> null
            is AppTransitionPhase.Opening -> phase.item
            is AppTransitionPhase.Closing -> phase.item
        }

        if (bounds != null && item != null && containerSize.value.width > 0) {
            val t = progress.value.coerceIn(0f, 1.08f)
            val clampedT = t.coerceIn(0f, 1f)
            val fullW = containerSize.value.width.toFloat()
            val fullH = containerSize.value.height.toFloat()

            val currentLeft = lerp(bounds.left, 0f, t)
            val currentTop = lerp(bounds.top, 0f, t)
            val currentWidth = lerp(bounds.width, fullW, t).coerceAtLeast(1f)
            val currentHeight = lerp(bounds.height, fullH, t).coerceAtLeast(1f)

            val startRadiusPx = bounds.width * (settings.iconCornerRadiusPercent / 100f)
            val midRadiusPx = with(density) { 36.dp.toPx() }
            val cornerPx = if (clampedT < 0.7f) {
                lerp(startRadiusPx, midRadiusPx, clampedT / 0.7f)
            } else {
                lerp(midRadiusPx, 0f, (clampedT - 0.7f) / 0.3f)
            }
            val cornerDp = with(density) { cornerPx.toDp() }
            val currentWidthDp = with(density) { currentWidth.toDp() }
            val currentHeightDp = with(density) { currentHeight.toDp() }

            val windowAlpha = when (phase) {
                is AppTransitionPhase.Opening -> lerp(0.15f, 1f, (clampedT * 1.4f).coerceAtMost(1f))
                is AppTransitionPhase.Closing -> lerp(0f, 1f, (clampedT * 1.3f).coerceAtMost(1f))
                AppTransitionPhase.Idle -> 0f
            }

            val iconLayerAlpha = (1f - (clampedT * 1.55f)).coerceIn(0f, 1f)
            val surfaceBg = if (settings.darkIconTheme) Color(0xFF1C1C1E) else Color(0xFFF5F5F7)

            Box(
                modifier = Modifier
                    .offset { IntOffset(currentLeft.roundToInt(), currentTop.roundToInt()) }
                    .size(currentWidthDp, currentHeightDp)
                    .clip(RoundedCornerShape(cornerDp))
                    .background(surfaceBg.copy(alpha = windowAlpha)),
                contentAlignment = Alignment.Center
            ) {
                if (iconLayerAlpha > 0.02f) {
                    val iconSizeDp = with(density) { bounds.width.toDp() }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .alpha(iconLayerAlpha),
                        contentAlignment = Alignment.Center
                    ) {
                        IosAppIcon(
                            packageName = item.packageName,
                            label = item.label,
                            iconSize = iconSizeDp,
                            cornerRadiusPercent = settings.iconCornerRadiusPercent,
                            darkThemeMode = settings.darkIconTheme,
                            forceIosIconPack = settings.forceIosIconPack,
                            customOverride = item.customIosIconKey
                        )
                    }
                }
            }
        }
    }
}
