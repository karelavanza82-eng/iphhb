package com.example.dock

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.database.HomeItemEntity
import com.example.database.LauncherSettings
import com.example.icons.IosAppIcon
import com.example.notifications.IosNotificationBadge

@Composable
fun FrostedDock(
    dockItems: List<HomeItemEntity>,
    settings: LauncherSettings,
    badgeCounts: Map<String, Int>,
    isEditMode: Boolean,
    jiggleRotation: Float,
    draggingItemId: String?,
    onRegisterIconBounds: (String, Rect) -> Unit,
    onRegisterDockBounds: (Rect) -> Unit,
    onIconClick: (HomeItemEntity, Rect) -> Unit,
    onEnterEditMode: () -> Unit,
    onRemoveItem: (HomeItemEntity) -> Unit,
    onDragStart: (HomeItemEntity, Offset) -> Unit,
    onDragDelta: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    modifier: Modifier = Modifier
) {
    val iconSize: Dp = settings.iconSizeDp.dp
    val dockShape = RoundedCornerShape(30.dp)
    val sortedDock = remember(dockItems) { dockItems.sortedBy { it.gridCol }.take(4) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .onGloballyPositioned { coords ->
                onRegisterDockBounds(coords.boundsInRoot())
            }
            .clip(dockShape)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = (settings.dockOpacity + 0.08f).coerceAtMost(0.65f)),
                        Color(0xFF1C1C1E).copy(alpha = settings.dockOpacity.coerceIn(0.10f, 0.55f))
                    )
                )
            )
            .border(
                width = 0.75.dp,
                color = Color.White.copy(alpha = 0.28f),
                shape = dockShape
            )
            .padding(horizontal = 16.dp, vertical = 14.dp)
            .testTag("frosted_dock")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (slotCol in 0 until 4) {
                val item = sortedDock.find { it.gridCol == slotCol }
                    ?: sortedDock.getOrNull(slotCol)
                if (item != null) {
                    var pressed by remember { mutableStateOf(false) }
                    var latestBounds by remember { mutableStateOf(Rect.Zero) }
                    val pressScale by animateFloatAsState(
                        targetValue = if (pressed) 0.90f else 1f,
                        animationSpec = spring(),
                        label = "dock_press"
                    )
                    val isDraggingThis = draggingItemId == item.id

                    Box(
                        modifier = Modifier
                            .size(iconSize + 10.dp)
                            .graphicsLayer {
                                alpha = if (isDraggingThis) 0.25f else 1f
                                scaleX = pressScale
                                scaleY = pressScale
                                rotationZ = if (isEditMode) jiggleRotation else 0f
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(iconSize)
                                .onGloballyPositioned { coords ->
                                    val rect = coords.boundsInRoot()
                                    latestBounds = rect
                                    onRegisterIconBounds(item.id, rect)
                                }
                                .pointerInput(item.id, isEditMode) {
                                    detectTapGestures(
                                        onPress = {
                                            pressed = true
                                            tryAwaitRelease()
                                            pressed = false
                                        },
                                        onTap = {
                                            if (!isEditMode) {
                                                onIconClick(item, latestBounds)
                                            }
                                        },
                                        onLongPress = {
                                            if (!isEditMode) onEnterEditMode()
                                        }
                                    )
                                }
                                .then(
                                    if (isEditMode && !settings.lockLayout) {
                                        Modifier.pointerInput(item.id) {
                                            detectDragGesturesAfterLongPress(
                                                onDragStart = { offset ->
                                                    val globalStart = Offset(
                                                        latestBounds.left + offset.x,
                                                        latestBounds.top + offset.y
                                                    )
                                                    onDragStart(item, globalStart)
                                                },
                                                onDrag = { change, dragAmount ->
                                                    change.consume()
                                                    onDragDelta(dragAmount)
                                                },
                                                onDragEnd = onDragEnd,
                                                onDragCancel = onDragEnd
                                            )
                                        }
                                    } else Modifier
                                )
                                .testTag("dock_icon_${item.packageName}")
                        ) {
                            IosAppIcon(
                                packageName = item.packageName,
                                label = item.label,
                                iconSize = iconSize,
                                cornerRadiusPercent = settings.iconCornerRadiusPercent,
                                darkThemeMode = settings.darkIconTheme,
                                forceIosIconPack = settings.forceIosIconPack,
                                customOverride = item.customIosIconKey
                            )
                        }

                        val count = badgeCounts[item.packageName] ?: 0
                        if (!isEditMode && count > 0) {
                            IosNotificationBadge(
                                count = count,
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(top = 0.dp, end = 0.dp)
                            )
                        }

                        if (isEditMode) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFD1D1D6).copy(alpha = 0.92f))
                                    .pointerInput(item.id) {
                                        detectTapGestures { onRemoveItem(item) }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Remove,
                                    contentDescription = "Remove ${item.label}",
                                    tint = Color(0xFF1C1C1E),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                } else {
                    Box(modifier = Modifier.size(iconSize + 10.dp))
                }
            }
        }
    }
}
