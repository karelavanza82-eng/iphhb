package com.example

import com.example.icons.IosIconKey
import com.example.icons.IosIconMapper
import org.junit.Assert.assertEquals
import org.junit.Test

class LauncherCoreUnitTest {

    @Test
    fun iconMapper_mapsAllCoreIosAppsAccurately() {
        assertEquals(IosIconKey.PHONE, IosIconMapper.resolveIconKey("com.google.android.dialer", "Phone"))
        assertEquals(IosIconKey.MESSAGES, IosIconMapper.resolveIconKey("com.google.android.apps.messaging", "Messages"))
        assertEquals(IosIconKey.CHROME, IosIconMapper.resolveIconKey("com.android.chrome", "Chrome"))
        assertEquals(IosIconKey.SAFARI, IosIconMapper.resolveIconKey("com.sec.android.app.sbrowser", "Internet"))
        assertEquals(IosIconKey.MUSIC, IosIconMapper.resolveIconKey("com.google.android.apps.youtube.music", "YT Music"))
        assertEquals(IosIconKey.PHOTOS, IosIconMapper.resolveIconKey("com.google.android.apps.photos", "Photos"))
        assertEquals(IosIconKey.CAMERA, IosIconMapper.resolveIconKey("com.google.android.GoogleCamera", "Camera"))
        assertEquals(IosIconKey.SETTINGS, IosIconMapper.resolveIconKey("com.android.settings", "Settings"))
        assertEquals(IosIconKey.CALENDAR, IosIconMapper.resolveIconKey("com.google.android.calendar", "Calendar"))
        assertEquals(IosIconKey.CLOCK, IosIconMapper.resolveIconKey("com.google.android.deskclock", "Clock"))
        assertEquals(IosIconKey.MAPS, IosIconMapper.resolveIconKey("com.google.android.apps.maps", "Maps"))
        assertEquals(IosIconKey.APP_STORE, IosIconMapper.resolveIconKey("com.android.vending", "Play Store"))
        assertEquals(IosIconKey.WHATSAPP, IosIconMapper.resolveIconKey("com.whatsapp", "WhatsApp"))
        assertEquals(IosIconKey.INSTAGRAM, IosIconMapper.resolveIconKey("com.instagram.android", "Instagram"))
        assertEquals(IosIconKey.TIKTOK, IosIconMapper.resolveIconKey("com.zhiliaoapp.musically", "TikTok"))
        assertEquals(IosIconKey.YOUTUBE, IosIconMapper.resolveIconKey("com.google.android.youtube", "YouTube"))
        assertEquals(IosIconKey.WALLET, IosIconMapper.resolveIconKey("com.google.android.apps.walletnfcrel", "Wallet"))
        assertEquals(IosIconKey.CALCULATOR, IosIconMapper.resolveIconKey("com.google.android.calculator", "Calculator"))
        assertEquals(IosIconKey.GOOGLE, IosIconMapper.resolveIconKey("com.google.android.googlequicksearchbox", "Google"))
        assertEquals(IosIconKey.CUSTOM_FALLBACK, IosIconMapper.resolveIconKey("com.acme.customutility", "Utility"))
    }
}
