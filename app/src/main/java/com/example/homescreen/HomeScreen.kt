package com.example.homescreen

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.animations.compatibleBlur
import com.example.database.HomeItemEntity
import com.example.database.LabelColorMode
import com.example.database.LauncherSettings
import com.example.database.PageTransitionEffect
import com.example.database.WallpaperPreset
import com.example.dock.FrostedDock
import com.example.icons.IosAppIcon
import com.example.notifications.IosNotificationBadge
import com.example.ui.theme.IosSansFontFamily
import com.example.wallpaper.WallpaperRepository
import com.example.widgets.AppWidgetHostManager
import com.example.widgets.CalendarWidgetData
import com.example.widgets.IosWidgetCard
import com.example.widgets.WeatherUiState
import kotlinx.coroutines.launch
import java.io.File
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

@Composable
fun HomeScreen(
    homeItems: List<HomeItemEntity>,
    settings: LauncherSettings,
    badgeCounts: Map<String, Int>,
    weatherState: WeatherUiState,
    calendarData: CalendarWidgetData,
    isEditMode: Boolean,
    transitionProgress: Float,
    isSearchOpen: Boolean,
    wallpaperRepository: WallpaperRepository,
    widgetHostManager: AppWidgetHostManager,
    onRegisterIconBounds: (String, Rect) -> Unit,
    onLaunchItem: (HomeItemEntity, Rect) -> Unit,
    onWidgetClick: (HomeItemEntity) -> Unit,
    onEnterEditMode: () -> Unit,
    onExitEditMode: () -> Unit,
    onMoveItem: (draggedId: String, targetPage: Int, targetRow: Int, targetCol: Int) -> Unit,
    onRemoveItem: (HomeItemEntity) -> Unit,
    onOpenSearch: () -> Unit,
    onOpenWidgetPicker: () -> Unit,
    onOpenWallpaperPicker: () -> Unit,
    onOpenSettings: () -> Unit
) {
    BackHandler(enabled = isEditMode) {
        onExitEditMode()
    }

    val density = LocalDensity.current
    val scope = rememberCoroutineScope()

    val dockItems = remember(homeItems) {
        homeItems.filter { it.pageIndex == -1 }.sortedBy { it.gridCol }
    }
    val pageItems = remember(homeItems) {
        homeItems.filter { it.pageIndex >= 0 }
    }
    val pageCount = remember(pageItems) {
        ((pageItems.maxOfOrNull { it.pageIndex } ?: 0) + 1).coerceAtLeast(1)
    }

    val pagerState = rememberPagerState(pageCount = { pageCount })

    // Jiggle animation for iOS Edit Mode
    val jiggleTransition = rememberInfiniteTransition(label = "jiggle")
    val jiggleAngle by jiggleTransition.animateFloat(
        initialValue = -1.7f,
        targetValue = 1.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 135, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "jiggle_angle"
    )
    val effectiveJiggle = if (isEditMode) jiggleAngle else 0f

    // Drag-and-drop shared state across grid & dock
    var draggedItem by remember { mutableStateOf<HomeItemEntity?>(null) }
    var dragFingerPos by remember { mutableStateOf(Offset.Zero) }
    var gridBounds by remember { mutableStateOf(Rect.Zero) }
    var dockBounds by remember { mutableStateOf(Rect.Zero) }
    var edgeTurnCooldown by remember { mutableStateOf(0L) }

    val wallpaperBlurDp = when {
        isSearchOpen -> 22.dp
        transitionProgress > 0.01f && settings.blurWallpaperOnLaunch -> (transitionProgress * 24f).dp
        else -> 0.dp
    }
    val iconsBlurDp = if (transitionProgress > 0.01f && settings.blurIconOnLaunch) {
        (transitionProgress * 14f).dp
    } else 0.dp

    val labelColor = when (settings.labelColorMode) {
        LabelColorMode.WHITE -> Color.White
        LabelColorMode.BLACK -> Color(0xFF1C1C1E)
        LabelColorMode.AUTO -> Color.White
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(isEditMode, isSearchOpen) {
                var accumY = 0f
                detectVerticalDragGestures(
                    onDragStart = { accumY = 0f },
                    onVerticalDrag = { _, dragAmount ->
                        accumY += dragAmount
                        if (accumY > 110f && !isEditMode && !isSearchOpen) {
                            accumY = -9999f
                            onOpenSearch()
                        }
                    }
                )
            }
            .pointerInput(isEditMode) {
                detectTapGestures(
                    onLongPress = { if (!isEditMode) onEnterEditMode() },
                    onTap = { if (isEditMode) onExitEditMode() }
                )
            }
    ) {
        // 1. Fullscreen Wallpaper Layer with dynamic blur & scale
        val wpModifier = Modifier
            .fillMaxSize()
            .compatibleBlur(wallpaperBlurDp, settings.lowEndGpuFallback)
            .graphicsLayer {
                val animZoom = 1f + transitionProgress * 0.06f
                scaleX = settings.wallpaperScale * animZoom
                scaleY = settings.wallpaperScale * animZoom
                translationX = settings.wallpaperOffsetX
                translationY = settings.wallpaperOffsetY
            }

        if (settings.wallpaperPreset == WallpaperPreset.CUSTOM_FILE &&
            settings.customWallpaperPath.isNotBlank() &&
            File(settings.customWallpaperPath).exists()
        ) {
            AsyncImage(
                model = File(settings.customWallpaperPath),
                contentDescription = "Home screen wallpaper",
                contentScale = ContentScale.Crop,
                modifier = wpModifier
            )
        } else {
            Image(
                painter = painterResource(
                    id = wallpaperRepository.getDrawableResForPreset(settings.wallpaperPreset)
                ),
                contentDescription = "Home screen wallpaper",
                contentScale = ContentScale.Crop,
                modifier = wpModifier
            )
        }

        // Low-end GPU fallback dim scrim during blur
        if (settings.lowEndGpuFallback && (transitionProgress > 0.05f || isSearchOpen)) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = (transitionProgress * 0.45f).coerceAtMost(0.55f)))
            )
        }

        // 2. Main Home Content (Status bar, Pager grid, Page Dots, Search Pill, Dock)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .compatibleBlur(iconsBlurDp, settings.lowEndGpuFallback)
                .graphicsLayer {
                    val scale = 1f - transitionProgress * 0.05f
                    scaleX = scale
                    scaleY = scale
                }
        ) {
            if (isEditMode) {
                // iPhone Edit Mode Top Bar (+ Widget & Done)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 18.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onOpenWidgetPicker,
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White.copy(alpha = 0.28f),
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("edit_mode_add_widget_btn")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Edit", fontFamily = IosSansFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }

                    Button(
                        onClick = onExitEditMode,
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White.copy(alpha = 0.28f),
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("edit_mode_done_btn")
                    ) {
                        Text("Done", fontFamily = IosSansFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }
                }
            } else {
                Spacer(Modifier.statusBarsPadding().height(6.dp))
            }

            // Multi-page Home Grid
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .onGloballyPositioned { gridBounds = it.boundsInRoot() }
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                    userScrollEnabled = draggedItem == null
                ) { pageIdx ->
                    val itemsOnPage = remember(pageItems, pageIdx) {
                        pageItems.filter { it.pageIndex == pageIdx }
                    }
                    val pageOffset = ((pagerState.currentPage - pageIdx) + pagerState.currentPageOffsetFraction)

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                when (settings.pageTransitionEffect) {
                                    PageTransitionEffect.SLIDE -> Unit
                                    PageTransitionEffect.DEPTH -> {
                                        val s = 1f - (pageOffset.absoluteValue * 0.14f).coerceIn(0f, 0.2f)
                                        scaleX = s
                                        scaleY = s
                                        alpha = 1f - (pageOffset.absoluteValue * 0.35f).coerceIn(0f, 0.5f)
                                    }
                                    PageTransitionEffect.CUBE -> {
                                        cameraDistance = 16f * density.density
                                        rotationY = -pageOffset * 28f
                                        transformOrigin = TransformOrigin(
                                            if (pageOffset < 0) 0f else 1f,
                                            0.5f
                                        )
                                    }
                                    PageTransitionEffect.FADE -> {
                                        alpha = 1f - pageOffset.absoluteValue.coerceIn(0f, 1f)
                                    }
                                }
                            }
                    ) {
                        HomeGridPage(
                            items = itemsOnPage,
                            settings = settings,
                            badgeCounts = badgeCounts,
                            weatherState = weatherState,
                            calendarData = calendarData,
                            isEditMode = isEditMode,
                            jiggleRotation = effectiveJiggle,
                            draggingItemId = draggedItem?.id,
                            widgetHostManager = widgetHostManager,
                            onRegisterIconBounds = onRegisterIconBounds,
                            onLaunchItem = onLaunchItem,
                            onWidgetClick = onWidgetClick,
                            onEnterEditMode = onEnterEditMode,
                            onRemoveItem = onRemoveItem,
                            onDragStart = { item, globalOffset ->
                                draggedItem = item
                                dragFingerPos = globalOffset
                            },
                            onDragDelta = { delta ->
                                dragFingerPos += delta
                                // Auto-flip pages when dragging near horizontal screen edges
                                val now = System.currentTimeMillis()
                                if (now - edgeTurnCooldown > 700L && gridBounds.width > 0) {
                                    if (dragFingerPos.x > gridBounds.right - 65f && pagerState.currentPage < pageCount - 1) {
                                        edgeTurnCooldown = now
                                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                                    } else if (dragFingerPos.x < gridBounds.left + 65f && pagerState.currentPage > 0) {
                                        edgeTurnCooldown = now
                                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
                                    }
                                }
                            },
                            onDragEnd = {
                                val item = draggedItem
                                if (item != null && gridBounds.width > 0) {
                                    if (dockBounds.contains(dragFingerPos) && item.itemType != "WIDGET") {
                                        val dockRelX = ((dragFingerPos.x - dockBounds.left) / dockBounds.width).coerceIn(0f, 0.99f)
                                        val targetCol = (dockRelX * 4).toInt().coerceIn(0, 3)
                                        onMoveItem(item.id, -1, 0, targetCol)
                                    } else {
                                        val cols = settings.gridColumns
                                        val rows = 6
                                        val relX = ((dragFingerPos.x - gridBounds.left) / gridBounds.width).coerceIn(0f, 0.99f)
                                        val relY = ((dragFingerPos.y - gridBounds.top) / gridBounds.height).coerceIn(0f, 0.99f)
                                        val targetCol = (relX * cols).toInt().coerceIn(0, cols - 1)
                                        val targetRow = (relY * rows).toInt().coerceIn(0, rows - 1)
                                        onMoveItem(item.id, pagerState.currentPage, targetRow, targetCol)
                                    }
                                }
                                draggedItem = null
                            }
                        )
                    }
                }
            }

            // Page indicator dots + Search pill (hidden during Edit Mode toolbar)
            if (!isEditMode) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (pageCount > 1) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            repeat(pageCount) { idx ->
                                val active = pagerState.currentPage == idx
                                Box(
                                    modifier = Modifier
                                        .size(if (active) 7.dp else 6.dp)
                                        .clip(CircleShape)
                                        .background(
                                            Color.White.copy(alpha = if (active) 0.96f else 0.36f)
                                        )
                                )
                            }
                        }
                    }

                    if (settings.showSearchPill) {
                        Row(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.22f))
                                .border(0.5.dp, Color.White.copy(alpha = 0.24f), CircleShape)
                                .clickable(onClick = onOpenSearch)
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                                .testTag("home_search_pill"),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = Color.White,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(Modifier.width(5.dp))
                            Text(
                                text = "Search",
                                color = Color.White,
                                fontFamily = IosSansFontFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // Bottom Dock OR iOS Edit Mode Floating Toolbar
            if (isEditMode) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .clip(RoundedCornerShape(26.dp))
                        .background(Color(0xFF1C1C1E).copy(alpha = 0.86f))
                        .border(0.6.dp, Color.White.copy(alpha = 0.20f), RoundedCornerShape(26.dp))
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    EditToolbarButton(
                        icon = Icons.Default.Palette,
                        label = "Wallpaper",
                        onClick = onOpenWallpaperPicker,
                        tag = "edit_toolbar_wallpaper"
                    )
                    EditToolbarButton(
                        icon = Icons.Default.Widgets,
                        label = "Widgets",
                        onClick = onOpenWidgetPicker,
                        tag = "edit_toolbar_widgets"
                    )
                    EditToolbarButton(
                        icon = Icons.Default.Settings,
                        label = "Settings",
                        onClick = onOpenSettings,
                        tag = "edit_toolbar_settings"
                    )
                }
            } else if (settings.showDock) {
                FrostedDock(
                    dockItems = dockItems,
                    settings = settings,
                    badgeCounts = badgeCounts,
                    isEditMode = isEditMode,
                    jiggleRotation = effectiveJiggle,
                    draggingItemId = draggedItem?.id,
                    onRegisterIconBounds = onRegisterIconBounds,
                    onRegisterDockBounds = { dockBounds = it },
                    onIconClick = onLaunchItem,
                    onEnterEditMode = onEnterEditMode,
                    onRemoveItem = onRemoveItem,
                    onDragStart = { item, pos ->
                        draggedItem = item
                        dragFingerPos = pos
                    },
                    onDragDelta = { delta -> dragFingerPos += delta },
                    onDragEnd = {
                        val item = draggedItem
                        if (item != null && gridBounds.width > 0) {
                            if (dockBounds.contains(dragFingerPos)) {
                                val dockRelX = ((dragFingerPos.x - dockBounds.left) / dockBounds.width).coerceIn(0f, 0.99f)
                                val targetCol = (dockRelX * 4).toInt().coerceIn(0, 3)
                                onMoveItem(item.id, -1, 0, targetCol)
                            } else if (gridBounds.contains(dragFingerPos)) {
                                val cols = settings.gridColumns
                                val rows = 6
                                val relX = ((dragFingerPos.x - gridBounds.left) / gridBounds.width).coerceIn(0f, 0.99f)
                                val relY = ((dragFingerPos.y - gridBounds.top) / gridBounds.height).coerceIn(0f, 0.99f)
                                val targetCol = (relX * cols).toInt().coerceIn(0, cols - 1)
                                val targetRow = (relY * rows).toInt().coerceIn(0, rows - 1)
                                onMoveItem(item.id, pagerState.currentPage, targetRow, targetCol)
                            }
                        }
                        draggedItem = null
                    }
                )
            } else {
                Spacer(Modifier.navigationBarsPadding().height(12.dp))
            }
        }

        // 3. Floating Drag Preview when dragging an icon or widget
        val activeDrag = draggedItem
        if (activeDrag != null) {
            val iconSizePx = with(density) { settings.iconSizeDp.dp.toPx() }
            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            (dragFingerPos.x - iconSizePx / 2f).roundToInt(),
                            (dragFingerPos.y - iconSizePx / 2f).roundToInt()
                        )
                    }
                    .graphicsLayer {
                        scaleX = 1.14f
                        scaleY = 1.14f
                        alpha = 0.92f
                    }
            ) {
                IosAppIcon(
                    packageName = activeDrag.packageName,
                    label = activeDrag.label,
                    iconSize = settings.iconSizeDp.dp,
                    cornerRadiusPercent = settings.iconCornerRadiusPercent,
                    darkThemeMode = settings.darkIconTheme,
                    forceIosIconPack = settings.forceIosIconPack,
                    customOverride = activeDrag.customIosIconKey
                )
            }
        }
    }
}

@Composable
private fun EditToolbarButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    tag: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag(tag)
    ) {
        Icon(icon, contentDescription = label, tint = Color.White, modifier = Modifier.size(22.dp))
        Spacer(Modifier.height(4.dp))
        Text(
            text = label,
            color = Color.White,
            fontFamily = IosSansFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp
        )
    }
}

@Composable
private fun HomeGridPage(
    items: List<HomeItemEntity>,
    settings: LauncherSettings,
    badgeCounts: Map<String, Int>,
    weatherState: WeatherUiState,
    calendarData: CalendarWidgetData,
    isEditMode: Boolean,
    jiggleRotation: Float,
    draggingItemId: String?,
    widgetHostManager: AppWidgetHostManager,
    onRegisterIconBounds: (String, Rect) -> Unit,
    onLaunchItem: (HomeItemEntity, Rect) -> Unit,
    onWidgetClick: (HomeItemEntity) -> Unit,
    onEnterEditMode: () -> Unit,
    onRemoveItem: (HomeItemEntity) -> Unit,
    onDragStart: (HomeItemEntity, Offset) -> Unit,
    onDragDelta: (Offset) -> Unit,
    onDragEnd: () -> Unit
) {
    val labelColor = when (settings.labelColorMode) {
        LabelColorMode.WHITE -> Color.White
        LabelColorMode.BLACK -> Color(0xFF1C1C1E)
        LabelColorMode.AUTO -> Color.White
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = settings.horizontalSpacingDp.dp, vertical = 6.dp)
    ) {
        val cols = settings.gridColumns.coerceIn(4, 5)
        val rows = 6
        val cellWidth = maxWidth / cols
        val cellHeight = maxHeight / rows
        val iconSize = settings.iconSizeDp.dp.coerceAtMost(cellWidth - 10.dp)

        items.forEach { item ->
            val col = item.gridCol.coerceIn(0, (cols - item.spanCols).coerceAtLeast(0))
            val row = item.gridRow.coerceIn(0, (rows - item.spanRows).coerceAtLeast(0))
            val xOffset = cellWidth * col
            val yOffset = cellHeight * row
            val itemW = cellWidth * item.spanCols
            val itemH = cellHeight * item.spanRows
            val isDraggingThis = draggingItemId == item.id

            if (item.itemType == "WIDGET") {
                val widgetHorizontalPad = 8.dp
                val widgetTopPad = 4.dp
                val labelSpace = if (settings.hideIconLabels) 4.dp else 20.dp
                val cardW = (itemW - widgetHorizontalPad * 2).coerceAtLeast(60.dp)
                val cardH = (itemH - widgetTopPad - labelSpace).coerceAtLeast(60.dp)

                Column(
                    modifier = Modifier
                        .offset(x = xOffset, y = yOffset)
                        .size(width = itemW, height = itemH)
                        .graphicsLayer {
                            alpha = if (isDraggingThis) 0.25f else 1f
                            rotationZ = if (isEditMode) jiggleRotation * 0.45f else 0f
                        },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .padding(top = widgetTopPad)
                            .size(width = cardW, height = cardH)
                            .then(
                                if (isEditMode && !settings.lockLayout) {
                                    Modifier.pointerInput(item.id) {
                                        detectDragGesturesAfterLongPress(
                                            onDragStart = { off -> onDragStart(item, off) },
                                            onDrag = { ch, d -> ch.consume(); onDragDelta(d) },
                                            onDragEnd = onDragEnd,
                                            onDragCancel = onDragEnd
                                        )
                                    }
                                } else Modifier
                            )
                    ) {
                        IosWidgetCard(
                            item = item,
                            weatherState = weatherState,
                            useCelsius = settings.weatherUseCelsius,
                            calendarData = calendarData,
                            photoWidgetUri = settings.photoWidgetUri,
                            hostManager = widgetHostManager,
                            widgetWidthDp = cardW.value.roundToInt(),
                            widgetHeightDp = cardH.value.roundToInt(),
                            onWidgetClick = {
                                if (!isEditMode) onWidgetClick(item)
                            },
                            modifier = Modifier.fillMaxSize()
                        )

                        if (isEditMode) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .offset(x = (-4).dp, y = (-4).dp)
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFD1D1D6).copy(alpha = 0.94f))
                                    .clickable { onRemoveItem(item) },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Remove,
                                    contentDescription = "Remove ${item.label}",
                                    tint = Color(0xFF1C1C1E),
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }
                    }

                    if (!settings.hideIconLabels) {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = item.label,
                            color = labelColor,
                            fontFamily = IosSansFontFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = settings.textSizeSp.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = TextStyle(
                                shadow = Shadow(Color.Black.copy(alpha = 0.45f), Offset(0f, 1f), 3f)
                            )
                        )
                    }
                }
            } else {
                var latestBounds by remember { mutableStateOf(Rect.Zero) }
                var pressed by remember { mutableStateOf(false) }

                Column(
                    modifier = Modifier
                        .offset(x = xOffset, y = yOffset)
                        .size(width = itemW, height = itemH)
                        .graphicsLayer {
                            alpha = if (isDraggingThis) 0.25f else 1f
                            val s = if (pressed) 0.91f else 1f
                            scaleX = s
                            scaleY = s
                            rotationZ = if (isEditMode) jiggleRotation else 0f
                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    Box(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .size(iconSize + 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(iconSize)
                                .onGloballyPositioned { coords ->
                                    val r = coords.boundsInRoot()
                                    latestBounds = r
                                    onRegisterIconBounds(item.id, r)
                                }
                                .pointerInput(item.id, isEditMode) {
                                    detectTapGestures(
                                        onPress = {
                                            pressed = true
                                            tryAwaitRelease()
                                            pressed = false
                                        },
                                        onTap = {
                                            if (!isEditMode) onLaunchItem(item, latestBounds)
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
                                                    onDragStart(
                                                        item,
                                                        Offset(
                                                            latestBounds.left + offset.x,
                                                            latestBounds.top + offset.y
                                                        )
                                                    )
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
                                .testTag("grid_icon_${item.packageName}")
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

                        val badgeCount = badgeCounts[item.packageName] ?: 0
                        if (!isEditMode && badgeCount > 0) {
                            IosNotificationBadge(
                                count = badgeCount,
                                modifier = Modifier.align(Alignment.TopEnd)
                            )
                        }

                        if (isEditMode) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFD1D1D6).copy(alpha = 0.94f))
                                    .clickable { onRemoveItem(item) }
                                    .testTag("remove_icon_${item.id}"),
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

                    if (!settings.hideIconLabels) {
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = item.label,
                            color = labelColor,
                            fontFamily = IosSansFontFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = settings.textSizeSp.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center,
                            style = TextStyle(
                                shadow = Shadow(Color.Black.copy(alpha = 0.45f), Offset(0f, 1f), 3f)
                            ),
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
