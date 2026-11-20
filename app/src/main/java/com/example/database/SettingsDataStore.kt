package com.example.database

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.launcherSettingsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "ios_launcher_settings"
)

enum class AnimationSpeed(val label: String, val durationScale: Float) {
    FAST("Fast", 0.72f),
    NORMAL("Normal", 1.0f),
    SMOOTH("Smooth", 1.35f)
}

enum class SpringIntensity(val label: String, val dampingRatio: Float, val stiffness: Float) {
    GENTLE("Gentle", 0.92f, 360f),
    BALANCED("Balanced", 0.82f, 440f),
    BOUNCY("Bouncy", 0.68f, 520f)
}

enum class WallpaperPreset(val id: String, val title: String) {
    BUILTIN_DARK("BUILTIN_DARK", "iOS Obsidian"),
    BUILTIN_VIOLET("BUILTIN_VIOLET", "iOS 18 Satin"),
    BUILTIN_AZURE("BUILTIN_AZURE", "iOS Azure Silk"),
    CUSTOM_FILE("CUSTOM_FILE", "Custom Photo")
}

enum class PageTransitionEffect(val id: String, val title: String) {
    SLIDE("SLIDE", "Slide"),
    DEPTH("DEPTH", "Depth Scale"),
    CUBE("CUBE", "3D Flip"),
    FADE("FADE", "Cross Fade")
}

enum class LabelColorMode(val id: String, val title: String) {
    AUTO("AUTO", "Automatic"),
    WHITE("WHITE", "White"),
    BLACK("BLACK", "Black")
}

enum class AppLanguage(val code: String, val title: String) {
    SYSTEM("SYSTEM", "System Default"),
    EN("EN", "English"),
    ID("ID", "Bahasa Indonesia")
}

data class LauncherSettings(
    val gridColumns: Int = 4,
    val iconSizeDp: Float = 60f,
    val horizontalSpacingDp: Float = 16f,
    val verticalSpacingDp: Float = 14f,
    val textSizeSp: Float = 12f,
    val hideIconLabels: Boolean = false,
    val labelColorMode: LabelColorMode = LabelColorMode.AUTO,
    val darkIconTheme: Boolean = false,
    val forceIosIconPack: Boolean = true,
    val iconCornerRadiusPercent: Int = 23,
    val pageTransitionEffect: PageTransitionEffect = PageTransitionEffect.SLIDE,
    val showSearchPill: Boolean = true,
    val showStatusBar: Boolean = true,
    val showDock: Boolean = true,
    val dockBlurEnabled: Boolean = true,
    val dockOpacity: Float = 0.24f,
    val animationsEnabled: Boolean = true,
    val appOpenAnimationEnabled: Boolean = true,
    val appCloseAnimationEnabled: Boolean = true,
    val blurIconOnLaunch: Boolean = true,
    val blurWallpaperOnLaunch: Boolean = true,
    val animationSpeed: AnimationSpeed = AnimationSpeed.NORMAL,
    val springIntensity: SpringIntensity = SpringIntensity.BALANCED,
    val lowEndGpuFallback: Boolean = false,
    val badgesEnabled: Boolean = true,
    val wallpaperPreset: WallpaperPreset = WallpaperPreset.BUILTIN_DARK,
    val customWallpaperPath: String = "",
    val wallpaperScale: Float = 1f,
    val wallpaperOffsetX: Float = 0f,
    val wallpaperOffsetY: Float = 0f,
    val weatherCity: String = "",
    val weatherLat: Double? = null,
    val weatherLon: Double? = null,
    val weatherUseCelsius: Boolean = true,
    val lockLayout: Boolean = false,
    val autoFillEmptySlots: Boolean = false,
    val searchShowRecents: Boolean = true,
    val searchIncludePackageName: Boolean = true,
    val language: AppLanguage = AppLanguage.SYSTEM,
    val photoWidgetUri: String = ""
)

class SettingsRepository(private val context: Context) {

    private object Keys {
        val GRID_COLUMNS = intPreferencesKey("grid_columns")
        val ICON_SIZE_DP = floatPreferencesKey("icon_size_dp")
        val H_SPACING_DP = floatPreferencesKey("h_spacing_dp")
        val V_SPACING_DP = floatPreferencesKey("v_spacing_dp")
        val TEXT_SIZE_SP = floatPreferencesKey("text_size_sp")
        val HIDE_LABELS = booleanPreferencesKey("hide_labels")
        val LABEL_COLOR_MODE = stringPreferencesKey("label_color_mode")
        val DARK_ICON_THEME = booleanPreferencesKey("dark_icon_theme")
        val FORCE_IOS_ICONS = booleanPreferencesKey("force_ios_icons")
        val CORNER_RADIUS_PCT = intPreferencesKey("corner_radius_pct")
        val PAGE_TRANSITION = stringPreferencesKey("page_transition")
        val SHOW_SEARCH_PILL = booleanPreferencesKey("show_search_pill")
        val SHOW_STATUS_BAR = booleanPreferencesKey("show_status_bar")
        val SHOW_DOCK = booleanPreferencesKey("show_dock")
        val DOCK_BLUR = booleanPreferencesKey("dock_blur")
        val DOCK_OPACITY = floatPreferencesKey("dock_opacity")
        val ANIMATIONS_ENABLED = booleanPreferencesKey("animations_enabled")
        val APP_OPEN_ANIM = booleanPreferencesKey("app_open_anim")
        val APP_CLOSE_ANIM = booleanPreferencesKey("app_close_anim")
        val BLUR_ICON_LAUNCH = booleanPreferencesKey("blur_icon_launch")
        val BLUR_WALLPAPER_LAUNCH = booleanPreferencesKey("blur_wallpaper_launch")
        val ANIM_SPEED = stringPreferencesKey("anim_speed")
        val SPRING_INTENSITY = stringPreferencesKey("spring_intensity")
        val LOW_END_GPU = booleanPreferencesKey("low_end_gpu")
        val BADGES_ENABLED = booleanPreferencesKey("badges_enabled")
        val WALLPAPER_PRESET = stringPreferencesKey("wallpaper_preset")
        val CUSTOM_WALLPAPER_PATH = stringPreferencesKey("custom_wallpaper_path")
        val WALLPAPER_SCALE = floatPreferencesKey("wallpaper_scale")
        val WALLPAPER_OFFSET_X = floatPreferencesKey("wallpaper_offset_x")
        val WALLPAPER_OFFSET_Y = floatPreferencesKey("wallpaper_offset_y")
        val WEATHER_CITY = stringPreferencesKey("weather_city")
        val WEATHER_LAT = stringPreferencesKey("weather_lat")
        val WEATHER_LON = stringPreferencesKey("weather_lon")
        val WEATHER_CELSIUS = booleanPreferencesKey("weather_celsius")
        val LOCK_LAYOUT = booleanPreferencesKey("lock_layout")
        val AUTO_FILL_SLOTS = booleanPreferencesKey("auto_fill_slots")
        val SEARCH_SHOW_RECENTS = booleanPreferencesKey("search_show_recents")
        val SEARCH_INCLUDE_PKG = booleanPreferencesKey("search_include_pkg")
        val LANGUAGE = stringPreferencesKey("language")
        val PHOTO_WIDGET_URI = stringPreferencesKey("photo_widget_uri")
    }

    val settingsFlow: Flow<LauncherSettings> = context.launcherSettingsDataStore.data.map { prefs ->
        LauncherSettings(
            gridColumns = prefs[Keys.GRID_COLUMNS] ?: 4,
            iconSizeDp = prefs[Keys.ICON_SIZE_DP] ?: 60f,
            horizontalSpacingDp = prefs[Keys.H_SPACING_DP] ?: 16f,
            verticalSpacingDp = prefs[Keys.V_SPACING_DP] ?: 14f,
            textSizeSp = prefs[Keys.TEXT_SIZE_SP] ?: 12f,
            hideIconLabels = prefs[Keys.HIDE_LABELS] ?: false,
            labelColorMode = prefs[Keys.LABEL_COLOR_MODE]?.let { runCatching { LabelColorMode.valueOf(it) }.getOrNull() }
                ?: LabelColorMode.AUTO,
            darkIconTheme = prefs[Keys.DARK_ICON_THEME] ?: false,
            forceIosIconPack = prefs[Keys.FORCE_IOS_ICONS] ?: true,
            iconCornerRadiusPercent = prefs[Keys.CORNER_RADIUS_PCT] ?: 23,
            pageTransitionEffect = prefs[Keys.PAGE_TRANSITION]?.let { runCatching { PageTransitionEffect.valueOf(it) }.getOrNull() }
                ?: PageTransitionEffect.SLIDE,
            showSearchPill = prefs[Keys.SHOW_SEARCH_PILL] ?: true,
            showStatusBar = prefs[Keys.SHOW_STATUS_BAR] ?: true,
            showDock = prefs[Keys.SHOW_DOCK] ?: true,
            dockBlurEnabled = prefs[Keys.DOCK_BLUR] ?: true,
            dockOpacity = prefs[Keys.DOCK_OPACITY] ?: 0.24f,
            animationsEnabled = prefs[Keys.ANIMATIONS_ENABLED] ?: true,
            appOpenAnimationEnabled = prefs[Keys.APP_OPEN_ANIM] ?: true,
            appCloseAnimationEnabled = prefs[Keys.APP_CLOSE_ANIM] ?: true,
            blurIconOnLaunch = prefs[Keys.BLUR_ICON_LAUNCH] ?: true,
            blurWallpaperOnLaunch = prefs[Keys.BLUR_WALLPAPER_LAUNCH] ?: true,
            animationSpeed = prefs[Keys.ANIM_SPEED]?.let { runCatching { AnimationSpeed.valueOf(it) }.getOrNull() }
                ?: AnimationSpeed.NORMAL,
            springIntensity = prefs[Keys.SPRING_INTENSITY]?.let { runCatching { SpringIntensity.valueOf(it) }.getOrNull() }
                ?: SpringIntensity.BALANCED,
            lowEndGpuFallback = prefs[Keys.LOW_END_GPU] ?: false,
            badgesEnabled = prefs[Keys.BADGES_ENABLED] ?: true,
            wallpaperPreset = prefs[Keys.WALLPAPER_PRESET]?.let { runCatching { WallpaperPreset.valueOf(it) }.getOrNull() }
                ?: WallpaperPreset.BUILTIN_DARK,
            customWallpaperPath = prefs[Keys.CUSTOM_WALLPAPER_PATH] ?: "",
            wallpaperScale = prefs[Keys.WALLPAPER_SCALE] ?: 1f,
            wallpaperOffsetX = prefs[Keys.WALLPAPER_OFFSET_X] ?: 0f,
            wallpaperOffsetY = prefs[Keys.WALLPAPER_OFFSET_Y] ?: 0f,
            weatherCity = prefs[Keys.WEATHER_CITY] ?: "",
            weatherLat = prefs[Keys.WEATHER_LAT]?.toDoubleOrNull(),
            weatherLon = prefs[Keys.WEATHER_LON]?.toDoubleOrNull(),
            weatherUseCelsius = prefs[Keys.WEATHER_CELSIUS] ?: true,
            lockLayout = prefs[Keys.LOCK_LAYOUT] ?: false,
            autoFillEmptySlots = prefs[Keys.AUTO_FILL_SLOTS] ?: false,
            searchShowRecents = prefs[Keys.SEARCH_SHOW_RECENTS] ?: true,
            searchIncludePackageName = prefs[Keys.SEARCH_INCLUDE_PKG] ?: true,
            language = prefs[Keys.LANGUAGE]?.let { runCatching { AppLanguage.valueOf(it) }.getOrNull() }
                ?: AppLanguage.SYSTEM,
            photoWidgetUri = prefs[Keys.PHOTO_WIDGET_URI] ?: ""
        )
    }

    suspend fun update(transform: (LauncherSettings) -> LauncherSettings) {
        context.launcherSettingsDataStore.edit { prefs ->
            val current = LauncherSettings(
                gridColumns = prefs[Keys.GRID_COLUMNS] ?: 4,
                iconSizeDp = prefs[Keys.ICON_SIZE_DP] ?: 60f,
                horizontalSpacingDp = prefs[Keys.H_SPACING_DP] ?: 16f,
                verticalSpacingDp = prefs[Keys.V_SPACING_DP] ?: 14f,
                textSizeSp = prefs[Keys.TEXT_SIZE_SP] ?: 12f,
                hideIconLabels = prefs[Keys.HIDE_LABELS] ?: false,
                labelColorMode = prefs[Keys.LABEL_COLOR_MODE]?.let { runCatching { LabelColorMode.valueOf(it) }.getOrNull() }
                    ?: LabelColorMode.AUTO,
                darkIconTheme = prefs[Keys.DARK_ICON_THEME] ?: false,
                forceIosIconPack = prefs[Keys.FORCE_IOS_ICONS] ?: true,
                iconCornerRadiusPercent = prefs[Keys.CORNER_RADIUS_PCT] ?: 23,
                pageTransitionEffect = prefs[Keys.PAGE_TRANSITION]?.let { runCatching { PageTransitionEffect.valueOf(it) }.getOrNull() }
                    ?: PageTransitionEffect.SLIDE,
                showSearchPill = prefs[Keys.SHOW_SEARCH_PILL] ?: true,
                showStatusBar = prefs[Keys.SHOW_STATUS_BAR] ?: true,
                showDock = prefs[Keys.SHOW_DOCK] ?: true,
                dockBlurEnabled = prefs[Keys.DOCK_BLUR] ?: true,
                dockOpacity = prefs[Keys.DOCK_OPACITY] ?: 0.24f,
                animationsEnabled = prefs[Keys.ANIMATIONS_ENABLED] ?: true,
                appOpenAnimationEnabled = prefs[Keys.APP_OPEN_ANIM] ?: true,
                appCloseAnimationEnabled = prefs[Keys.APP_CLOSE_ANIM] ?: true,
                blurIconOnLaunch = prefs[Keys.BLUR_ICON_LAUNCH] ?: true,
                blurWallpaperOnLaunch = prefs[Keys.BLUR_WALLPAPER_LAUNCH] ?: true,
                animationSpeed = prefs[Keys.ANIM_SPEED]?.let { runCatching { AnimationSpeed.valueOf(it) }.getOrNull() }
                    ?: AnimationSpeed.NORMAL,
                springIntensity = prefs[Keys.SPRING_INTENSITY]?.let { runCatching { SpringIntensity.valueOf(it) }.getOrNull() }
                    ?: SpringIntensity.BALANCED,
                lowEndGpuFallback = prefs[Keys.LOW_END_GPU] ?: false,
                badgesEnabled = prefs[Keys.BADGES_ENABLED] ?: true,
                wallpaperPreset = prefs[Keys.WALLPAPER_PRESET]?.let { runCatching { WallpaperPreset.valueOf(it) }.getOrNull() }
                    ?: WallpaperPreset.BUILTIN_DARK,
                customWallpaperPath = prefs[Keys.CUSTOM_WALLPAPER_PATH] ?: "",
                wallpaperScale = prefs[Keys.WALLPAPER_SCALE] ?: 1f,
                wallpaperOffsetX = prefs[Keys.WALLPAPER_OFFSET_X] ?: 0f,
                wallpaperOffsetY = prefs[Keys.WALLPAPER_OFFSET_Y] ?: 0f,
                weatherCity = prefs[Keys.WEATHER_CITY] ?: "",
                weatherLat = prefs[Keys.WEATHER_LAT]?.toDoubleOrNull(),
                weatherLon = prefs[Keys.WEATHER_LON]?.toDoubleOrNull(),
                weatherUseCelsius = prefs[Keys.WEATHER_CELSIUS] ?: true,
                lockLayout = prefs[Keys.LOCK_LAYOUT] ?: false,
                autoFillEmptySlots = prefs[Keys.AUTO_FILL_SLOTS] ?: false,
                searchShowRecents = prefs[Keys.SEARCH_SHOW_RECENTS] ?: true,
                searchIncludePackageName = prefs[Keys.SEARCH_INCLUDE_PKG] ?: true,
                language = prefs[Keys.LANGUAGE]?.let { runCatching { AppLanguage.valueOf(it) }.getOrNull() }
                    ?: AppLanguage.SYSTEM,
                photoWidgetUri = prefs[Keys.PHOTO_WIDGET_URI] ?: ""
            )
            val updated = transform(current)
            prefs[Keys.GRID_COLUMNS] = updated.gridColumns
            prefs[Keys.ICON_SIZE_DP] = updated.iconSizeDp
            prefs[Keys.H_SPACING_DP] = updated.horizontalSpacingDp
            prefs[Keys.V_SPACING_DP] = updated.verticalSpacingDp
            prefs[Keys.TEXT_SIZE_SP] = updated.textSizeSp
            prefs[Keys.HIDE_LABELS] = updated.hideIconLabels
            prefs[Keys.LABEL_COLOR_MODE] = updated.labelColorMode.name
            prefs[Keys.DARK_ICON_THEME] = updated.darkIconTheme
            prefs[Keys.FORCE_IOS_ICONS] = updated.forceIosIconPack
            prefs[Keys.CORNER_RADIUS_PCT] = updated.iconCornerRadiusPercent
            prefs[Keys.PAGE_TRANSITION] = updated.pageTransitionEffect.name
            prefs[Keys.SHOW_SEARCH_PILL] = updated.showSearchPill
            prefs[Keys.SHOW_STATUS_BAR] = updated.showStatusBar
            prefs[Keys.SHOW_DOCK] = updated.showDock
            prefs[Keys.DOCK_BLUR] = updated.dockBlurEnabled
            prefs[Keys.DOCK_OPACITY] = updated.dockOpacity
            prefs[Keys.ANIMATIONS_ENABLED] = updated.animationsEnabled
            prefs[Keys.APP_OPEN_ANIM] = updated.appOpenAnimationEnabled
            prefs[Keys.APP_CLOSE_ANIM] = updated.appCloseAnimationEnabled
            prefs[Keys.BLUR_ICON_LAUNCH] = updated.blurIconOnLaunch
            prefs[Keys.BLUR_WALLPAPER_LAUNCH] = updated.blurWallpaperOnLaunch
            prefs[Keys.ANIM_SPEED] = updated.animationSpeed.name
            prefs[Keys.SPRING_INTENSITY] = updated.springIntensity.name
            prefs[Keys.LOW_END_GPU] = updated.lowEndGpuFallback
            prefs[Keys.BADGES_ENABLED] = updated.badgesEnabled
            prefs[Keys.WALLPAPER_PRESET] = updated.wallpaperPreset.name
            prefs[Keys.CUSTOM_WALLPAPER_PATH] = updated.customWallpaperPath
            prefs[Keys.WALLPAPER_SCALE] = updated.wallpaperScale
            prefs[Keys.WALLPAPER_OFFSET_X] = updated.wallpaperOffsetX
            prefs[Keys.WALLPAPER_OFFSET_Y] = updated.wallpaperOffsetY
            prefs[Keys.WEATHER_CITY] = updated.weatherCity
            if (updated.weatherLat != null) prefs[Keys.WEATHER_LAT] = updated.weatherLat.toString() else prefs.remove(Keys.WEATHER_LAT)
            if (updated.weatherLon != null) prefs[Keys.WEATHER_LON] = updated.weatherLon.toString() else prefs.remove(Keys.WEATHER_LON)
            prefs[Keys.WEATHER_CELSIUS] = updated.weatherUseCelsius
            prefs[Keys.LOCK_LAYOUT] = updated.lockLayout
            prefs[Keys.AUTO_FILL_SLOTS] = updated.autoFillEmptySlots
            prefs[Keys.SEARCH_SHOW_RECENTS] = updated.searchShowRecents
            prefs[Keys.SEARCH_INCLUDE_PKG] = updated.searchIncludePackageName
            prefs[Keys.LANGUAGE] = updated.language.name
            prefs[Keys.PHOTO_WIDGET_URI] = updated.photoWidgetUri
        }
    }
}
