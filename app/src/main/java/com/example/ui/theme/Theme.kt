package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = IosBlue,
    secondary = IosGreen,
    tertiary = IosOrange,
    error = IosRed,
    background = IosSettingsBackgroundLight,
    surface = IosSettingsSurfaceLight,
    onPrimary = IosPrimaryTextDark,
    onBackground = IosPrimaryTextLight,
    onSurface = IosPrimaryTextLight,
    onSurfaceVariant = IosSecondaryTextLight,
    outlineVariant = IosSettingsDividerLight
)

private val DarkColors = darkColorScheme(
    primary = IosBlue,
    secondary = IosGreen,
    tertiary = IosOrange,
    error = IosRed,
    background = IosSettingsBackgroundDark,
    surface = IosSettingsSurfaceDark,
    onPrimary = IosPrimaryTextDark,
    onBackground = IosPrimaryTextDark,
    onSurface = IosPrimaryTextDark,
    onSurfaceVariant = IosSecondaryTextDark,
    outlineVariant = IosSettingsDividerDark
)

@Composable
fun IosLauncherTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = IosLauncherTypography,
        content = content
    )
}
