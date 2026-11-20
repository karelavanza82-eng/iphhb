package com.example.settings

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.BlurOn
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dock
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PhotoSizeSelectLarge
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.database.AppLanguage
import com.example.database.LauncherSettings
import com.example.permissions.PermissionsManager
import com.example.ui.theme.IosBlue
import com.example.ui.theme.IosGreen
import com.example.ui.theme.IosIndigo
import com.example.ui.theme.IosOrange
import com.example.ui.theme.IosPurple
import com.example.ui.theme.IosRed
import com.example.ui.theme.IosSansFontFamily
import com.example.ui.theme.IosTeal

enum class SettingsRoute(val title: String) {
    MAIN("Settings"),
    HOME_SCREEN("Home Screen"),
    APP_ICONS("App Icons"),
    ICON_SIZE_GRID("Icon Size & Grid"),
    WIDGETS("Widget Settings"),
    DOCK("Dock Settings"),
    NOTIFICATION_BADGES("Notification Badges"),
    ANIMATION("App Opening / Closing"),
    BLUR("Blur Settings"),
    SEARCH("Search Settings"),
    LANGUAGE("Language"),
    BACKUP_RESTORE("Backup & Restore"),
    ABOUT("About"),
    WEATHER_CONFIG("Weather Location")
}

@Composable
fun SettingsMainScreen(
    settings: LauncherSettings,
    onNavigate: (SettingsRoute) -> Unit,
    onOpenWallpaper: () -> Unit,
    onRequestDefaultHome: () -> Unit,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)
    val context = LocalContext.current
    val isDefaultHome = PermissionsManager.isDefaultLauncher(context)
    val isId = settings.language == AppLanguage.ID

    IosSettingsScaffold(
        title = if (isId) "Pengaturan" else "Settings",
        onBack = onBack,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Default HOME Launcher status card
            IosSettingsGroup {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onRequestDefaultHome() }
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isDefaultHome) Icons.Default.CheckCircle else Icons.Default.Home,
                        contentDescription = null,
                        tint = if (isDefaultHome) IosGreen else IosBlue,
                        modifier = Modifier.size(30.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isDefaultHome) {
                                if (isId) "Launcher Default Aktif" else "Default Home Launcher Active"
                            } else {
                                if (isId) "Jadikan Launcher Utama" else "Set as Default Home App"
                            },
                            color = Color(0xFF1C1C1E),
                            fontFamily = IosSansFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = if (isDefaultHome) {
                                "iOS Launcher handles the Android Home button"
                            } else {
                                "Tap to register as official Android HOME screen"
                            },
                            color = Color(0xFF8E8E93),
                            fontFamily = IosSansFontFamily,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            IosSettingsGroup(header = if (isId) "Layar Utama & Tampilan" else "Home Screen & Appearance") {
                IosNavRow(
                    title = if (isId) "Layar Utama" else "Home Screen",
                    icon = Icons.Default.Home,
                    iconTint = IosBlue,
                    testTag = "nav_home_screen",
                    onClick = { onNavigate(SettingsRoute.HOME_SCREEN) }
                )
                IosDivider()
                IosNavRow(
                    title = if (isId) "Gaya Ikon" else "App Icons",
                    icon = Icons.Default.Apps,
                    iconTint = IosPurple,
                    value = if (settings.forceIosIconPack) "iOS Pack" else "System",
                    testTag = "nav_app_icons",
                    onClick = { onNavigate(SettingsRoute.APP_ICONS) }
                )
                IosDivider()
                IosNavRow(
                    title = if (isId) "Ukuran Ikon & Grid" else "Icon Size & Grid",
                    icon = Icons.Default.GridView,
                    iconTint = IosTeal,
                    value = "${settings.gridColumns}x6 • ${settings.iconSizeDp.toInt()}dp",
                    testTag = "nav_icon_grid",
                    onClick = { onNavigate(SettingsRoute.ICON_SIZE_GRID) }
                )
                IosDivider()
                IosNavRow(
                    title = "Widget Settings",
                    icon = Icons.Default.Widgets,
                    iconTint = IosOrange,
                    testTag = "nav_widgets",
                    onClick = { onNavigate(SettingsRoute.WIDGETS) }
                )
                IosDivider()
                IosNavRow(
                    title = if (isId) "Pengaturan Dock" else "Dock Settings",
                    icon = Icons.Default.Dock,
                    iconTint = IosIndigo,
                    value = if (settings.showDock) "On" else "Off",
                    testTag = "nav_dock",
                    onClick = { onNavigate(SettingsRoute.DOCK) }
                )
                IosDivider()
                IosNavRow(
                    title = "Wallpaper",
                    icon = Icons.Default.Wallpaper,
                    iconTint = Color(0xFF30B0C7),
                    value = settings.wallpaperPreset.title,
                    testTag = "nav_wallpaper",
                    onClick = onOpenWallpaper
                )
            }

            IosSettingsGroup(header = if (isId) "Animasi & Notifikasi" else "Motion & Notifications") {
                IosNavRow(
                    title = if (isId) "Badge Notifikasi" else "Notification Badges",
                    icon = Icons.Default.Notifications,
                    iconTint = IosRed,
                    value = if (settings.badgesEnabled) "On" else "Off",
                    testTag = "nav_badges",
                    onClick = { onNavigate(SettingsRoute.NOTIFICATION_BADGES) }
                )
                IosDivider()
                IosNavRow(
                    title = if (isId) "Animasi Buka/Tutup Aplikasi" else "Animation",
                    icon = Icons.Default.Animation,
                    iconTint = IosGreen,
                    value = if (settings.animationsEnabled) settings.animationSpeed.label else "Off",
                    testTag = "nav_animation",
                    onClick = { onNavigate(SettingsRoute.ANIMATION) }
                )
                IosDivider()
                IosNavRow(
                    title = "Blur Settings",
                    icon = Icons.Default.BlurOn,
                    iconTint = Color(0xFF5856D6),
                    testTag = "nav_blur",
                    onClick = { onNavigate(SettingsRoute.BLUR) }
                )
            }

            IosSettingsGroup(header = if (isId) "Sistem" else "System") {
                IosNavRow(
                    title = "Search Settings",
                    icon = Icons.Default.Search,
                    iconTint = Color(0xFF8E8E93),
                    testTag = "nav_search",
                    onClick = { onNavigate(SettingsRoute.SEARCH) }
                )
                IosDivider()
                IosNavRow(
                    title = if (isId) "Bahasa" else "Language",
                    icon = Icons.Default.Language,
                    iconTint = IosBlue,
                    value = settings.language.title,
                    testTag = "nav_language",
                    onClick = { onNavigate(SettingsRoute.LANGUAGE) }
                )
                IosDivider()
                IosNavRow(
                    title = "Backup & Restore",
                    icon = Icons.Default.Backup,
                    iconTint = Color(0xFF34C759),
                    testTag = "nav_backup",
                    onClick = { onNavigate(SettingsRoute.BACKUP_RESTORE) }
                )
                IosDivider()
                IosNavRow(
                    title = "About",
                    icon = Icons.Default.Info,
                    iconTint = Color(0xFF636366),
                    testTag = "nav_about",
                    onClick = { onNavigate(SettingsRoute.ABOUT) }
                )
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}
