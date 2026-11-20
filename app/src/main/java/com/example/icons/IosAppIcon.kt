package com.example.icons

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun IosAppIcon(
    packageName: String,
    label: String,
    iconSize: Dp,
    cornerRadiusPercent: Int = 23,
    darkThemeMode: Boolean = false,
    forceIosIconPack: Boolean = true,
    customOverride: String = "",
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val iconKey = remember(packageName, label, customOverride, forceIosIconPack) {
        if (forceIosIconPack) {
            IosIconMapper.resolveIconKey(packageName, label, customOverride)
        } else {
            IosIconKey.CUSTOM_FALLBACK
        }
    }

    val shape = RoundedCornerShape(percent = cornerRadiusPercent.coerceIn(12, 34))

    Box(
        modifier = modifier
            .size(iconSize)
            .clip(shape)
            .border(
                width = 0.5.dp,
                color = Color.White.copy(alpha = if (darkThemeMode) 0.12f else 0.16f),
                shape = shape
            ),
        contentAlignment = Alignment.Center
    ) {
        when (iconKey) {
            IosIconKey.PHONE,
            IosIconKey.MESSAGES,
            IosIconKey.SAFARI,
            IosIconKey.MUSIC,
            IosIconKey.PHOTOS,
            IosIconKey.CAMERA,
            IosIconKey.SETTINGS,
            IosIconKey.CALENDAR,
            IosIconKey.WEATHER,
            IosIconKey.CLOCK,
            IosIconKey.MAPS,
            IosIconKey.APP_STORE,
            IosIconKey.FACETIME,
            IosIconKey.PODCASTS,
            IosIconKey.HEALTH -> {
                DrawIosIconCatalogA(iconKey, iconSize, darkThemeMode)
            }

            IosIconKey.WALLET,
            IosIconKey.NOTES,
            IosIconKey.REMINDERS,
            IosIconKey.TV,
            IosIconKey.FILES,
            IosIconKey.MAIL,
            IosIconKey.CONTACTS,
            IosIconKey.CALCULATOR,
            IosIconKey.GOOGLE,
            IosIconKey.YOUTUBE,
            IosIconKey.WHATSAPP,
            IosIconKey.INSTAGRAM,
            IosIconKey.TIKTOK,
            IosIconKey.CHROME,
            IosIconKey.HOME,
            IosIconKey.SPOTIFY,
            IosIconKey.TELEGRAM,
            IosIconKey.FACEBOOK,
            IosIconKey.X_TWITTER -> {
                DrawIosIconCatalogB(iconKey, darkThemeMode)
            }

            IosIconKey.CUSTOM_FALLBACK -> {
                val cached = remember(packageName) {
                    IosIconMapper.loadCachedFallbackIcon(context, packageName)
                }
                if (cached != null) {
                    val bgBrush = if (darkThemeMode) {
                        Brush.verticalGradient(listOf(Color(0xFF2C2C2E), Color(0xFF1C1C1E)))
                    } else {
                        Brush.verticalGradient(listOf(cached.accentTop, cached.accentBottom))
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(bgBrush)
                            .padding(iconSize * 0.11f),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            bitmap = cached.bitmap,
                            contentDescription = label,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(percent = 20))
                        )
                    }
                } else {
                    DrawIosIconCatalogA(IosIconKey.APP_STORE, iconSize, darkThemeMode)
                }
            }
        }
    }
}
