package com.example.navigation

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.animations.AppTransitionOverlay
import com.example.database.HomeItemEntity
import com.example.homescreen.HomeScreen
import com.example.launcher.LauncherViewModel
import com.example.permissions.PermissionsManager
import com.example.search.SpotlightSearchOverlay
import com.example.settings.SettingsDetailScreen
import com.example.settings.SettingsMainScreen
import com.example.settings.SettingsRoute
import com.example.wallpaper.WallpaperPickerScreen
import com.example.wallpaper.WallpaperRepository
import com.example.widgets.IosWidgetKind
import com.example.widgets.WidgetPickerSheet

private enum class ActiveOverlay {
    NONE,
    SETTINGS,
    WALLPAPER
}

@Composable
fun LauncherRoot(
    viewModel: LauncherViewModel,
    wallpaperRepository: WallpaperRepository
) {
    val context = LocalContext.current
    val homeItems by viewModel.homeItems.collectAsStateWithLifecycle()
    val installedApps by viewModel.installedApps.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val badgeCounts by viewModel.filteredBadgeCounts.collectAsStateWithLifecycle()
    val badgePrefs by viewModel.badgePrefsMap.collectAsStateWithLifecycle()
    val recentSearches by viewModel.recentSearches.collectAsStateWithLifecycle()
    val weatherState by viewModel.weatherState.collectAsStateWithLifecycle()
    val calendarData by viewModel.calendarData.collectAsStateWithLifecycle()
    val isEditMode by viewModel.isEditMode.collectAsStateWithLifecycle()
    val transitionPhase by viewModel.transitionPhase.collectAsStateWithLifecycle()

    var activeOverlay by remember { mutableStateOf(ActiveOverlay.NONE) }
    val settingsStack = remember { mutableStateListOf(SettingsRoute.MAIN) }
    var isSearchOpen by remember { mutableStateOf(false) }
    var isWidgetPickerOpen by remember { mutableStateOf(false) }
    var transitionProgress by remember { mutableFloatStateOf(0f) }

    var editingNoteWidget by remember { mutableStateOf<HomeItemEntity?>(null) }
    var noteDraft by remember { mutableStateOf("") }

    // Pending Android AppWidget bind request
    var pendingBindInfo by remember { mutableStateOf<Triple<Int, AppWidgetProviderInfo, Pair<Int, Int>>?>(null) }
    val widgetBindLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val pending = pendingBindInfo
        pendingBindInfo = null
        if (pending != null) {
            val (widgetId, provider, spans) = pending
            if (result.resultCode == Activity.RESULT_OK) {
                viewModel.finalizeBoundAndroidWidget(widgetId, provider, spans.first, spans.second)
            } else {
                viewModel.widgetHostManager.deleteAppWidgetId(widgetId)
            }
        }
    }

    val defaultHomeLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { }

    val calendarPermLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) viewModel.refreshCalendar()
    }

    val photoWidgetPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.updateSettings { it.copy(photoWidgetUri = uri.toString()) }
        }
    }

    fun openSettings(initialRoute: SettingsRoute = SettingsRoute.MAIN) {
        viewModel.setEditMode(false)
        settingsStack.clear()
        settingsStack.add(SettingsRoute.MAIN)
        if (initialRoute != SettingsRoute.MAIN) {
            settingsStack.add(initialRoute)
        }
        activeOverlay = ActiveOverlay.SETTINGS
    }

    fun closeSettingsToHome() {
        activeOverlay = ActiveOverlay.NONE
        val originItemId = viewModel.clearPendingInternalRoute()
        viewModel.triggerInternalClosingAnimation(originItemId)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        HomeScreen(
            homeItems = homeItems,
            settings = settings,
            badgeCounts = badgeCounts,
            weatherState = weatherState,
            calendarData = calendarData,
            isEditMode = isEditMode,
            transitionProgress = transitionProgress,
            isSearchOpen = isSearchOpen,
            wallpaperRepository = wallpaperRepository,
            widgetHostManager = viewModel.widgetHostManager,
            onRegisterIconBounds = viewModel::registerIconBounds,
            onLaunchItem = { item, bounds ->
                viewModel.startAppOpenTransition(item, bounds)
            },
            onWidgetClick = { widgetItem ->
                val kind = runCatching { IosWidgetKind.valueOf(widgetItem.widgetKind) }.getOrNull()
                when (kind) {
                    IosWidgetKind.WEATHER -> openSettings(SettingsRoute.WEATHER_CONFIG)
                    IosWidgetKind.CALENDAR -> {
                        if (!PermissionsManager.hasCalendarPermission(context)) {
                            calendarPermLauncher.launch(android.Manifest.permission.READ_CALENDAR)
                        } else {
                            installedApps.firstOrNull {
                                it.packageName.contains("calendar", ignoreCase = true)
                            }?.let { viewModel.launchInstalledApp(it) }
                        }
                    }
                    IosWidgetKind.CLOCK -> {
                        installedApps.firstOrNull {
                            it.packageName.contains("clock", ignoreCase = true)
                        }?.let { viewModel.launchInstalledApp(it) }
                    }
                    IosWidgetKind.PHOTO -> {
                        photoWidgetPicker.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                    IosWidgetKind.MUSIC -> {
                        installedApps.firstOrNull {
                            it.packageName.contains("music", ignoreCase = true) ||
                                it.packageName.contains("spotify", ignoreCase = true)
                        }?.let { viewModel.launchInstalledApp(it) }
                    }
                    IosWidgetKind.CUSTOM -> {
                        editingNoteWidget = widgetItem
                        noteDraft = widgetItem.label
                    }
                    else -> Unit
                }
            },
            onEnterEditMode = { viewModel.setEditMode(true) },
            onExitEditMode = { viewModel.setEditMode(false) },
            onMoveItem = viewModel::moveOrSwapItem,
            onRemoveItem = viewModel::removeItemFromHome,
            onOpenSearch = { isSearchOpen = true },
            onOpenWidgetPicker = { isWidgetPickerOpen = true },
            onOpenWallpaperPicker = {
                viewModel.setEditMode(false)
                activeOverlay = ActiveOverlay.WALLPAPER
            },
            onOpenSettings = { openSettings(SettingsRoute.MAIN) }
        )

        // Spotlight Search Overlay
        AnimatedVisibility(
            visible = isSearchOpen,
            enter = fadeIn() + slideInVertically { -it / 8 },
            exit = fadeOut() + slideOutVertically { -it / 8 }
        ) {
            SpotlightSearchOverlay(
                installedApps = installedApps,
                recentSearches = recentSearches,
                badgeCounts = badgeCounts,
                settings = settings,
                onLaunchApp = viewModel::launchInstalledApp,
                onRecordSearch = viewModel::recordRecentSearch,
                onClearRecents = viewModel::clearRecentSearches,
                onDismiss = { isSearchOpen = false }
            )
        }

        // Settings Screen Overlay (with nested iOS sub-screen stack)
        AnimatedVisibility(
            visible = activeOverlay == ActiveOverlay.SETTINGS,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            val currentRoute = settingsStack.lastOrNull() ?: SettingsRoute.MAIN
            if (currentRoute == SettingsRoute.MAIN) {
                SettingsMainScreen(
                    settings = settings,
                    onNavigate = { route -> settingsStack.add(route) },
                    onOpenWallpaper = { activeOverlay = ActiveOverlay.WALLPAPER },
                    onRequestDefaultHome = {
                        defaultHomeLauncher.launch(
                            PermissionsManager.createRequestDefaultHomeIntent(context)
                        )
                    },
                    onBack = ::closeSettingsToHome
                )
            } else {
                AnimatedVisibility(
                    visible = true,
                    enter = slideInHorizontally { it / 3 } + fadeIn(),
                    exit = slideOutHorizontally { it / 3 } + fadeOut()
                ) {
                    SettingsDetailScreen(
                        route = currentRoute,
                        settings = settings,
                        installedApps = installedApps,
                        homeItems = homeItems,
                        badgePrefs = badgePrefs,
                        weatherRepository = viewModel.weatherRepository,
                        onUpdateSettings = viewModel::updateSettings,
                        onSetBadgePref = viewModel::setBadgeEnabledForPackage,
                        onRestoreHiddenApp = viewModel::addAppBackToHome,
                        onResetLayout = viewModel::resetHomeLayout,
                        onOpenWidgetPicker = {
                            activeOverlay = ActiveOverlay.NONE
                            isWidgetPickerOpen = true
                        },
                        onClearRecentSearches = viewModel::clearRecentSearches,
                        onExportBackup = viewModel::exportBackup,
                        onImportBackup = viewModel::importBackup,
                        onSelectWeatherCity = viewModel::setWeatherCity,
                        onUseDeviceWeatherLocation = viewModel::useDeviceLocationForWeather,
                        onCalendarPermissionGranted = viewModel::refreshCalendar,
                        onBack = {
                            if (settingsStack.size > 1) {
                                settingsStack.removeAt(settingsStack.lastIndex)
                            } else {
                                closeSettingsToHome()
                            }
                        }
                    )
                }
            }
        }

        // Wallpaper Picker & Interactive Crop Overlay
        AnimatedVisibility(
            visible = activeOverlay == ActiveOverlay.WALLPAPER,
            enter = slideInVertically { it / 5 } + fadeIn(),
            exit = slideOutVertically { it / 5 } + fadeOut()
        ) {
            WallpaperPickerScreen(
                settings = settings,
                wallpaperRepository = wallpaperRepository,
                onApplyWallpaper = viewModel::applyWallpaper,
                onBack = { activeOverlay = ActiveOverlay.NONE }
            )
        }

        // Widget Gallery Bottom Sheet
        if (isWidgetPickerOpen) {
            WidgetPickerSheet(
                hostManager = viewModel.widgetHostManager,
                onAddIosWidget = viewModel::addIosWidget,
                onAddAndroidWidget = { provider, spanCols, spanRows ->
                    viewModel.tryAddAndroidWidget(provider, spanCols, spanRows) { id, info, c, r ->
                        pendingBindInfo = Triple(id, info, c to r)
                        widgetBindLauncher.launch(
                            viewModel.widgetHostManager.createBindIntent(id, info)
                        )
                    }
                },
                onDismiss = { isWidgetPickerOpen = false }
            )
        }

        // Quick Note Widget Editor Dialog
        val noteTarget = editingNoteWidget
        if (noteTarget != null) {
            AlertDialog(
                onDismissRequest = { editingNoteWidget = null },
                title = { Text("Quick Note Widget") },
                text = {
                    OutlinedTextField(
                        value = noteDraft,
                        onValueChange = { noteDraft = it },
                        label = { Text("Note text") },
                        maxLines = 4
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.updateWidgetNote(noteTarget.id, noteDraft)
                        editingNoteWidget = null
                    }) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { editingNoteWidget = null }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Shared-Element Spring App Opening / Closing Transition Overlay
        AppTransitionOverlay(
            phase = transitionPhase,
            settings = settings,
            onProgressChanged = { transitionProgress = it },
            onOpeningReadyToLaunch = { item ->
                viewModel.onTransitionReadyToLaunch(item) { routeKey ->
                    when (routeKey) {
                        "settings" -> openSettings(SettingsRoute.MAIN)
                        "weather_config" -> openSettings(SettingsRoute.WEATHER_CONFIG)
                    }
                }
            },
            onTransitionFinished = {
                transitionProgress = 0f
                viewModel.onTransitionFinished()
            }
        )
    }
}
