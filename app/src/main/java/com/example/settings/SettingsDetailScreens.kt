package com.example.settings

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.database.AnimationSpeed
import com.example.database.AppLanguage
import com.example.database.HomeItemEntity
import com.example.database.LabelColorMode
import com.example.database.LauncherSettings
import com.example.database.PageTransitionEffect
import com.example.database.SpringIntensity
import com.example.icons.IosAppIcon
import com.example.launcher.InstalledAppInfo
import com.example.notifications.NotificationBadgeRepository
import com.example.permissions.PermissionsManager
import com.example.ui.theme.IosBlue
import com.example.ui.theme.IosGreen
import com.example.ui.theme.IosRed
import com.example.ui.theme.IosSansFontFamily
import com.example.widgets.CitySearchResult
import com.example.widgets.WeatherRepository
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun SettingsDetailScreen(
    route: SettingsRoute,
    settings: LauncherSettings,
    installedApps: List<InstalledAppInfo>,
    homeItems: List<HomeItemEntity>,
    badgePrefs: Map<String, Boolean>,
    weatherRepository: WeatherRepository,
    onUpdateSettings: ((LauncherSettings) -> LauncherSettings) -> Unit,
    onSetBadgePref: (String, Boolean) -> Unit,
    onRestoreHiddenApp: (InstalledAppInfo) -> Unit,
    onResetLayout: () -> Unit,
    onOpenWidgetPicker: () -> Unit,
    onClearRecentSearches: () -> Unit,
    onExportBackup: suspend () -> String,
    onImportBackup: suspend (String) -> Boolean,
    onSelectWeatherCity: (CitySearchResult) -> Unit,
    onUseDeviceWeatherLocation: () -> Unit,
    onCalendarPermissionGranted: () -> Unit,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    IosSettingsScaffold(
        title = route.title,
        onBack = onBack,
        modifier = Modifier.fillMaxSize()
    ) {
        when (route) {
            SettingsRoute.ANIMATION -> AnimationSettingsContent(settings, onUpdateSettings)
            SettingsRoute.BLUR -> BlurSettingsContent(settings, onUpdateSettings)
            SettingsRoute.HOME_SCREEN -> HomeScreenSettingsContent(
                settings = settings,
                installedApps = installedApps,
                homeItems = homeItems,
                onUpdateSettings = onUpdateSettings,
                onRestoreHiddenApp = onRestoreHiddenApp,
                onResetLayout = {
                    onResetLayout()
                    Toast.makeText(context, "Home layout reset", Toast.LENGTH_SHORT).show()
                }
            )
            SettingsRoute.APP_ICONS -> AppIconsSettingsContent(settings, onUpdateSettings)
            SettingsRoute.ICON_SIZE_GRID -> IconSizeGridSettingsContent(settings, onUpdateSettings)
            SettingsRoute.WIDGETS -> WidgetSettingsContent(
                settings = settings,
                onUpdateSettings = onUpdateSettings,
                onOpenWidgetPicker = onOpenWidgetPicker,
                onCalendarPermissionGranted = onCalendarPermissionGranted
            )
            SettingsRoute.DOCK -> DockSettingsContent(settings, onUpdateSettings)
            SettingsRoute.NOTIFICATION_BADGES -> NotificationBadgesSettingsContent(
                settings = settings,
                installedApps = installedApps,
                badgePrefs = badgePrefs,
                onUpdateSettings = onUpdateSettings,
                onSetBadgePref = onSetBadgePref
            )
            SettingsRoute.SEARCH -> SearchSettingsContent(
                settings = settings,
                onUpdateSettings = onUpdateSettings,
                onClearRecents = {
                    onClearRecentSearches()
                    Toast.makeText(context, "Recent searches cleared", Toast.LENGTH_SHORT).show()
                }
            )
            SettingsRoute.LANGUAGE -> LanguageSettingsContent(settings, onUpdateSettings)
            SettingsRoute.BACKUP_RESTORE -> BackupRestoreContent(
                onExportBackup = onExportBackup,
                onImportBackup = onImportBackup,
                scope = scope
            )
            SettingsRoute.ABOUT -> AboutSettingsContent()
            SettingsRoute.WEATHER_CONFIG -> WeatherConfigContent(
                settings = settings,
                weatherRepository = weatherRepository,
                onSelectCity = {
                    onSelectWeatherCity(it)
                    onBack()
                },
                onUseDeviceLocation = {
                    onUseDeviceWeatherLocation()
                    onBack()
                }
            )
            SettingsRoute.MAIN -> Unit
        }
    }
}

@Composable
private fun AnimationSettingsContent(
    settings: LauncherSettings,
    onUpdateSettings: ((LauncherSettings) -> LauncherSettings) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        IosSettingsGroup(header = "App Launch & Close") {
            IosToggleRow(
                title = "Enable Animations",
                checked = settings.animationsEnabled,
                onCheckedChange = { v -> onUpdateSettings { it.copy(animationsEnabled = v) } },
                testTag = "toggle_master_animations"
            )
            IosSimpleDivider()
            IosToggleRow(
                title = "App Opening Animation",
                checked = settings.appOpenAnimationEnabled,
                onCheckedChange = { v -> onUpdateSettings { it.copy(appOpenAnimationEnabled = v) } },
                testTag = "toggle_open_animation"
            )
            IosSimpleDivider()
            IosToggleRow(
                title = "App Closing Animation",
                checked = settings.appCloseAnimationEnabled,
                onCheckedChange = { v -> onUpdateSettings { it.copy(appCloseAnimationEnabled = v) } },
                testTag = "toggle_close_animation"
            )
        }

        IosSettingsGroup(
            header = "Blur During Transition",
            footer = "Matches iOS dynamic wallpaper and home icon blur during app zoom."
        ) {
            IosToggleRow(
                title = "Blur Icon",
                checked = settings.blurIconOnLaunch,
                onCheckedChange = { v -> onUpdateSettings { it.copy(blurIconOnLaunch = v) } },
                testTag = "toggle_blur_icon"
            )
            IosSimpleDivider()
            IosToggleRow(
                title = "Blur Wallpaper",
                checked = settings.blurWallpaperOnLaunch,
                onCheckedChange = { v -> onUpdateSettings { it.copy(blurWallpaperOnLaunch = v) } },
                testTag = "toggle_blur_wallpaper"
            )
        }

        IosSettingsGroup(header = "Animation Speed") {
            AnimationSpeed.entries.forEachIndexed { idx, speed ->
                if (idx > 0) IosSimpleDivider()
                SelectableCheckRow(
                    title = speed.label,
                    selected = settings.animationSpeed == speed,
                    onClick = { onUpdateSettings { it.copy(animationSpeed = speed) } }
                )
            }
        }

        IosSettingsGroup(header = "Spring Intensity") {
            SpringIntensity.entries.forEachIndexed { idx, spring ->
                if (idx > 0) IosSimpleDivider()
                SelectableCheckRow(
                    title = spring.label,
                    selected = settings.springIntensity == spring,
                    onClick = { onUpdateSettings { it.copy(springIntensity = spring) } }
                )
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun BlurSettingsContent(
    settings: LauncherSettings,
    onUpdateSettings: ((LauncherSettings) -> LauncherSettings) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        IosSettingsGroup(
            header = "Visual Blur Effects",
            footer = "Hardware RenderEffect blur requires Android 12+. Enable Low-End GPU Fallback on entry-level devices for maximum 60 FPS smoothness."
        ) {
            IosToggleRow(
                title = "Blur Wallpaper on Launch",
                checked = settings.blurWallpaperOnLaunch,
                onCheckedChange = { v -> onUpdateSettings { it.copy(blurWallpaperOnLaunch = v) } }
            )
            IosSimpleDivider()
            IosToggleRow(
                title = "Blur Icons on Launch",
                checked = settings.blurIconOnLaunch,
                onCheckedChange = { v -> onUpdateSettings { it.copy(blurIconOnLaunch = v) } }
            )
            IosSimpleDivider()
            IosToggleRow(
                title = "Dock Frosted Blur",
                checked = settings.dockBlurEnabled,
                onCheckedChange = { v -> onUpdateSettings { it.copy(dockBlurEnabled = v) } }
            )
            IosSimpleDivider()
            IosToggleRow(
                title = "Low-End GPU Fallback",
                subtitle = "Uses lightweight translucent scrim instead of shader blur",
                checked = settings.lowEndGpuFallback,
                onCheckedChange = { v -> onUpdateSettings { it.copy(lowEndGpuFallback = v) } }
            )
        }
    }
}

@Composable
private fun HomeScreenSettingsContent(
    settings: LauncherSettings,
    installedApps: List<InstalledAppInfo>,
    homeItems: List<HomeItemEntity>,
    onUpdateSettings: ((LauncherSettings) -> LauncherSettings) -> Unit,
    onRestoreHiddenApp: (InstalledAppInfo) -> Unit,
    onResetLayout: () -> Unit
) {
    val placedPkgs = remember(homeItems) { homeItems.map { it.packageName }.toSet() }
    val hiddenApps = remember(installedApps, placedPkgs) {
        installedApps.filter { it.packageName !in placedPkgs }
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            IosSettingsGroup(header = "Layout & Controls") {
                IosToggleRow(
                    title = "Lock Layout",
                    subtitle = "Prevent accidental icon moving",
                    checked = settings.lockLayout,
                    onCheckedChange = { v -> onUpdateSettings { it.copy(lockLayout = v) } }
                )
                IosSimpleDivider()
                IosToggleRow(
                    title = "Show Quick Search Pill",
                    checked = settings.showSearchPill,
                    onCheckedChange = { v -> onUpdateSettings { it.copy(showSearchPill = v) } }
                )
            }
        }

        item {
            IosSettingsGroup(header = "Page Flip Effect") {
                PageTransitionEffect.entries.forEachIndexed { idx, effect ->
                    if (idx > 0) IosSimpleDivider()
                    SelectableCheckRow(
                        title = effect.title,
                        selected = settings.pageTransitionEffect == effect,
                        onClick = { onUpdateSettings { it.copy(pageTransitionEffect = effect) } }
                    )
                }
            }
        }

        if (hiddenApps.isNotEmpty()) {
            item {
                IosSettingsGroup(header = "Removed from Home Screen (${hiddenApps.size})") {
                    hiddenApps.forEachIndexed { idx, app ->
                        if (idx > 0) IosSimpleDivider()
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onRestoreHiddenApp(app) }
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IosAppIcon(
                                packageName = app.packageName,
                                label = app.label,
                                iconSize = 36.dp
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(
                                text = app.label,
                                color = Color(0xFF1C1C1E),
                                fontFamily = IosSansFontFamily,
                                fontSize = 15.sp,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(Icons.Default.AddCircle, contentDescription = "Add back", tint = IosBlue)
                        }
                    }
                }
            }
        }

        item {
            IosSettingsGroup {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onResetLayout)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Reset Home Screen Layout",
                        color = IosRed,
                        fontFamily = IosSansFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    )
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun AppIconsSettingsContent(
    settings: LauncherSettings,
    onUpdateSettings: ((LauncherSettings) -> LauncherSettings) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        IosSettingsGroup(header = "iOS Icon Pack") {
            IosToggleRow(
                title = "iOS Artwork Icon Pack",
                subtitle = "Use authentic iPhone icon artwork for mapped apps",
                checked = settings.forceIosIconPack,
                onCheckedChange = { v -> onUpdateSettings { it.copy(forceIosIconPack = v) } }
            )
            IosSimpleDivider()
            IosToggleRow(
                title = "Dark Icon Style",
                checked = settings.darkIconTheme,
                onCheckedChange = { v -> onUpdateSettings { it.copy(darkIconTheme = v) } }
            )
            IosSimpleDivider()
            IosSliderRow(
                title = "Squircle Corner Radius",
                valueText = "${settings.iconCornerRadiusPercent}%",
                value = settings.iconCornerRadiusPercent.toFloat(),
                valueRange = 14f..32f,
                onValueChange = { v -> onUpdateSettings { it.copy(iconCornerRadiusPercent = v.roundToInt()) } }
            )
        }

        IosSettingsGroup(header = "App Labels") {
            IosToggleRow(
                title = "Hide Icon Labels",
                checked = settings.hideIconLabels,
                onCheckedChange = { v -> onUpdateSettings { it.copy(hideIconLabels = v) } },
                testTag = "toggle_hide_labels"
            )
            IosSimpleDivider()
            IosSliderRow(
                title = "Label Text Size",
                valueText = "${settings.textSizeSp.roundToInt()}sp",
                value = settings.textSizeSp,
                valueRange = 10f..15f,
                onValueChange = { v -> onUpdateSettings { it.copy(textSizeSp = v) } }
            )
        }

        IosSettingsGroup(header = "Label Color") {
            LabelColorMode.entries.forEachIndexed { idx, mode ->
                if (idx > 0) IosSimpleDivider()
                SelectableCheckRow(
                    title = mode.title,
                    selected = settings.labelColorMode == mode,
                    onClick = { onUpdateSettings { it.copy(labelColorMode = mode) } }
                )
            }
        }
    }
}

@Composable
private fun IconSizeGridSettingsContent(
    settings: LauncherSettings,
    onUpdateSettings: ((LauncherSettings) -> LauncherSettings) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        IosSettingsGroup(header = "Grid Columns") {
            listOf(4 to "4 Columns (iPhone Standard)", 5 to "5 Columns (Dense)").forEachIndexed { idx, (cols, label) ->
                if (idx > 0) IosSimpleDivider()
                SelectableCheckRow(
                    title = label,
                    selected = settings.gridColumns == cols,
                    onClick = { onUpdateSettings { it.copy(gridColumns = cols) } }
                )
            }
        }
        IosSettingsGroup(header = "Icon Size & Spacing") {
            IosSliderRow(
                title = "Icon Size",
                valueText = "${settings.iconSizeDp.roundToInt()}dp",
                value = settings.iconSizeDp,
                valueRange = 48f..68f,
                onValueChange = { v -> onUpdateSettings { it.copy(iconSizeDp = v) } }
            )
            IosSimpleDivider()
            IosSliderRow(
                title = "Horizontal Padding",
                valueText = "${settings.horizontalSpacingDp.roundToInt()}dp",
                value = settings.horizontalSpacingDp,
                valueRange = 8f..28f,
                onValueChange = { v -> onUpdateSettings { it.copy(horizontalSpacingDp = v) } }
            )
            IosSimpleDivider()
            IosSliderRow(
                title = "Vertical Spacing",
                valueText = "${settings.verticalSpacingDp.roundToInt()}dp",
                value = settings.verticalSpacingDp,
                valueRange = 8f..24f,
                onValueChange = { v -> onUpdateSettings { it.copy(verticalSpacingDp = v) } }
            )
        }
    }
}

@Composable
private fun WidgetSettingsContent(
    settings: LauncherSettings,
    onUpdateSettings: ((LauncherSettings) -> LauncherSettings) -> Unit,
    onOpenWidgetPicker: () -> Unit,
    onCalendarPermissionGranted: () -> Unit
) {
    val context = LocalContext.current
    var hasCalendar by remember { mutableStateOf(PermissionsManager.hasCalendarPermission(context)) }
    val calPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCalendar = granted
        if (granted) onCalendarPermissionGranted()
    }

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        IosSettingsGroup(header = "Weather Widget") {
            IosToggleRow(
                title = "Use Celsius (°C)",
                checked = settings.weatherUseCelsius,
                onCheckedChange = { v -> onUpdateSettings { it.copy(weatherUseCelsius = v) } }
            )
        }

        IosSettingsGroup(header = "Calendar Widget") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (!hasCalendar) {
                            calPermissionLauncher.launch(android.Manifest.permission.READ_CALENDAR)
                        }
                    }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Calendar Agenda Access", color = Color(0xFF1C1C1E), fontFamily = IosSansFontFamily, fontSize = 16.sp)
                Text(
                    text = if (hasCalendar) "Granted" else "Grant Access",
                    color = if (hasCalendar) IosGreen else IosBlue,
                    fontFamily = IosSansFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
            }
        }

        IosSettingsGroup {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onOpenWidgetPicker)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Open Widget Gallery",
                    color = IosBlue,
                    fontFamily = IosSansFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
private fun DockSettingsContent(
    settings: LauncherSettings,
    onUpdateSettings: ((LauncherSettings) -> LauncherSettings) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        IosSettingsGroup(header = "Frosted Glass Dock") {
            IosToggleRow(
                title = "Show Dock",
                checked = settings.showDock,
                onCheckedChange = { v -> onUpdateSettings { it.copy(showDock = v) } }
            )
            IosSimpleDivider()
            IosToggleRow(
                title = "Dock Blur Effect",
                checked = settings.dockBlurEnabled,
                onCheckedChange = { v -> onUpdateSettings { it.copy(dockBlurEnabled = v) } }
            )
            IosSimpleDivider()
            IosSliderRow(
                title = "Glass Opacity",
                valueText = "${(settings.dockOpacity * 100).roundToInt()}%",
                value = settings.dockOpacity,
                valueRange = 0.08f..0.55f,
                onValueChange = { v -> onUpdateSettings { it.copy(dockOpacity = v) } }
            )
        }
    }
}

@Composable
private fun NotificationBadgesSettingsContent(
    settings: LauncherSettings,
    installedApps: List<InstalledAppInfo>,
    badgePrefs: Map<String, Boolean>,
    onUpdateSettings: ((LauncherSettings) -> LauncherSettings) -> Unit,
    onSetBadgePref: (String, Boolean) -> Unit
) {
    val context = LocalContext.current
    val hasAccess = PermissionsManager.isNotificationAccessGranted(context)

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            IosSettingsGroup(
                header = "Notification Access",
                footer = "Android requires explicit Notification Access permission to read active notification counts for red badges."
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { NotificationBadgeRepository.openNotificationAccessSettings(context) }
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Notification Listener", color = Color(0xFF1C1C1E), fontFamily = IosSansFontFamily, fontSize = 16.sp)
                    Text(
                        text = if (hasAccess) "Connected" else "Enable Access",
                        color = if (hasAccess) IosGreen else IosBlue,
                        fontFamily = IosSansFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }
                IosSimpleDivider()
                IosToggleRow(
                    title = "Enable Red Number Badges",
                    checked = settings.badgesEnabled,
                    onCheckedChange = { v -> onUpdateSettings { it.copy(badgesEnabled = v) } }
                )
            }
        }

        item {
            Text(
                text = "PER-APP BADGE SETTINGS",
                color = Color(0xFF8E8E93),
                fontFamily = IosSansFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 30.dp, top = 8.dp, bottom = 6.dp)
            )
        }

        items(installedApps, key = { "${it.packageName}/${it.activityName}" }) { app ->
            val enabled = badgePrefs[app.packageName] ?: true
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IosAppIcon(
                    packageName = app.packageName,
                    label = app.label,
                    iconSize = 32.dp
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = app.label,
                    color = Color(0xFF1C1C1E),
                    fontFamily = IosSansFontFamily,
                    fontSize = 15.sp,
                    modifier = Modifier.weight(1f)
                )
                androidx.compose.material3.Switch(
                    checked = enabled,
                    onCheckedChange = { onSetBadgePref(app.packageName, it) }
                )
            }
        }

        item { Spacer(Modifier.height(24.dp)) }
    }
}

@Composable
private fun SearchSettingsContent(
    settings: LauncherSettings,
    onUpdateSettings: ((LauncherSettings) -> LauncherSettings) -> Unit,
    onClearRecents: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        IosSettingsGroup(header = "Spotlight Search") {
            IosToggleRow(
                title = "Show Recent Searches",
                checked = settings.searchShowRecents,
                onCheckedChange = { v -> onUpdateSettings { it.copy(searchShowRecents = v) } }
            )
            IosSimpleDivider()
            IosToggleRow(
                title = "Match Package Names",
                checked = settings.searchIncludePackageName,
                onCheckedChange = { v -> onUpdateSettings { it.copy(searchIncludePackageName = v) } }
            )
        }
        IosSettingsGroup {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onClearRecents)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text("Clear Recent Searches", color = IosRed, fontFamily = IosSansFontFamily, fontSize = 16.sp)
            }
        }
    }
}

@Composable
private fun LanguageSettingsContent(
    settings: LauncherSettings,
    onUpdateSettings: ((LauncherSettings) -> LauncherSettings) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        IosSettingsGroup(header = "Launcher Language") {
            AppLanguage.entries.forEachIndexed { idx, lang ->
                if (idx > 0) IosSimpleDivider()
                SelectableCheckRow(
                    title = lang.title,
                    selected = settings.language == lang,
                    onClick = { onUpdateSettings { it.copy(language = lang) } }
                )
            }
        }
    }
}

@Composable
private fun BackupRestoreContent(
    onExportBackup: suspend () -> String,
    onImportBackup: suspend (String) -> Boolean,
    scope: kotlinx.coroutines.CoroutineScope
) {
    val context = LocalContext.current

    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null) {
            scope.launch {
                val json = onExportBackup()
                val ok = runCatching {
                    context.contentResolver.openOutputStream(uri)?.use { out ->
                        out.write(json.toByteArray())
                    }
                    true
                }.getOrDefault(false)
                Toast.makeText(
                    context,
                    if (ok) "Backup exported successfully" else "Export failed",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            scope.launch {
                val json = runCatching {
                    context.contentResolver.openInputStream(uri)?.bufferedReader()?.readText()
                }.getOrNull()
                val ok = if (json != null) onImportBackup(json) else false
                Toast.makeText(
                    context,
                    if (ok) "Backup restored successfully" else "Invalid backup file",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        IosSettingsGroup(
            header = "Layout Backup",
            footer = "Saves your home screen pages, dock apps, and widget positions to a portable JSON file."
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { exportLauncher.launch("ios_launcher_backup.json") }
                    .padding(16.dp)
            ) {
                Text("Export Layout Backup", color = IosBlue, fontFamily = IosSansFontFamily, fontSize = 16.sp)
            }
            IosSimpleDivider()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { importLauncher.launch(arrayOf("application/json", "text/*")) }
                    .padding(16.dp)
            ) {
                Text("Restore Layout from File", color = IosBlue, fontFamily = IosSansFontFamily, fontSize = 16.sp)
            }
        }
    }
}

@Composable
private fun AboutSettingsContent() {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        IosSettingsGroup(header = "iOS Launcher") {
            AboutRow("Version", "1.0.0")
            IosSimpleDivider()
            AboutRow("Engine", "Native Kotlin + Jetpack Compose")
            IosSimpleDivider()
            AboutRow("Widget Host", "Android AppWidgetHost (ID 1024)")
            IosSimpleDivider()
            AboutRow("Badges", "NotificationListenerService")
        }
        IosSettingsGroup(
            header = "Android Transition Note",
            footer = "Opening external apps uses native Android ActivityOptions scale-up transitions paired with synchronized launcher wallpaper & icon blur. System-level gesture remote surfaces require privileged OEM permissions on Android 12+; returning to Home triggers the synchronized launcher icon zoom-back animation."
        ) {
            AboutRow("Transition Mode", "Synchronized Spring + ActivityOptions")
        }
    }
}

@Composable
private fun AboutRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color(0xFF1C1C1E), fontFamily = IosSansFontFamily, fontSize = 15.sp)
        Text(value, color = Color(0xFF8E8E93), fontFamily = IosSansFontFamily, fontSize = 14.sp)
    }
}

@Composable
private fun WeatherConfigContent(
    settings: LauncherSettings,
    weatherRepository: WeatherRepository,
    onSelectCity: (CitySearchResult) -> Unit,
    onUseDeviceLocation: () -> Unit
) {
    val context = LocalContext.current
    var query by remember { mutableStateOf(settings.weatherCity) }
    var results by remember { mutableStateOf<List<CitySearchResult>>(emptyList()) }
    val locLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) onUseDeviceLocation()
    }

    LaunchedEffect(query) {
        if (query.trim().length >= 2) {
            results = weatherRepository.searchCities(query)
        } else {
            results = emptyList()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(
            onClick = {
                if (PermissionsManager.hasCoarseLocationPermission(context)) {
                    onUseDeviceLocation()
                } else {
                    locLauncher.launch(android.Manifest.permission.ACCESS_COARSE_LOCATION)
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = IosBlue),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.MyLocation, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Use Current Location", fontFamily = IosSansFontFamily)
        }

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Search city (e.g. Jakarta, Lagos, Tokyo)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            items(results) { city ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .clickable { onSelectCity(city) }
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(city.name, fontFamily = IosSansFontFamily, fontWeight = FontWeight.SemiBold, color = Color(0xFF1C1C1E))
                        Text(
                            listOf(city.admin, city.country).filter { it.isNotBlank() }.joinToString(", "),
                            fontFamily = IosSansFontFamily,
                            fontSize = 12.sp,
                            color = Color(0xFF8E8E93)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SelectableCheckRow(
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = Color(0xFF1C1C1E),
            fontFamily = IosSansFontFamily,
            fontSize = 16.sp
        )
        if (selected) {
            Icon(Icons.Default.Check, contentDescription = "Selected", tint = IosBlue)
        }
    }
}
