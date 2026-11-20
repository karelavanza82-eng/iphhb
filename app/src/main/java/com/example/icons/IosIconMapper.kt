package com.example.icons

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.LruCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.ColorUtils
import java.util.Locale

data class CachedAppIcon(
    val bitmap: ImageBitmap,
    val accentTop: Color,
    val accentBottom: Color
)

object IosIconMapper {

    private val exactPackageMap: Map<String, IosIconKey> = mapOf(
        // Phone / Dialer
        "com.android.dialer" to IosIconKey.PHONE,
        "com.google.android.dialer" to IosIconKey.PHONE,
        "com.samsung.android.dialer" to IosIconKey.PHONE,
        "com.android.phone" to IosIconKey.PHONE,
        "com.transsion.dialer" to IosIconKey.PHONE,
        "com.miui.contacts" to IosIconKey.CONTACTS,

        // Messages
        "com.google.android.apps.messaging" to IosIconKey.MESSAGES,
        "com.android.mms" to IosIconKey.MESSAGES,
        "com.samsung.android.messaging" to IosIconKey.MESSAGES,
        "com.transsion.mms" to IosIconKey.MESSAGES,

        // Browsers
        "com.android.chrome" to IosIconKey.CHROME,
        "com.chrome.beta" to IosIconKey.CHROME,
        "com.sec.android.app.sbrowser" to IosIconKey.SAFARI,
        "org.mozilla.firefox" to IosIconKey.SAFARI,
        "com.opera.browser" to IosIconKey.SAFARI,
        "com.microsoft.emmx" to IosIconKey.SAFARI,
        "com.android.browser" to IosIconKey.SAFARI,

        // Music
        "com.google.android.apps.youtube.music" to IosIconKey.MUSIC,
        "com.apple.android.music" to IosIconKey.MUSIC,
        "com.sec.android.app.music" to IosIconKey.MUSIC,
        "com.miui.player" to IosIconKey.MUSIC,
        "com.spotify.music" to IosIconKey.SPOTIFY,

        // Photos / Gallery
        "com.google.android.apps.photos" to IosIconKey.PHOTOS,
        "com.android.gallery3d" to IosIconKey.PHOTOS,
        "com.sec.android.gallery3d" to IosIconKey.PHOTOS,
        "com.miui.gallery" to IosIconKey.PHOTOS,
        "com.transsion.gallery" to IosIconKey.PHOTOS,

        // Camera
        "com.android.camera" to IosIconKey.CAMERA,
        "com.android.camera2" to IosIconKey.CAMERA,
        "com.google.android.GoogleCamera" to IosIconKey.CAMERA,
        "com.sec.android.app.camera" to IosIconKey.CAMERA,
        "com.transsion.camera" to IosIconKey.CAMERA,
        "com.mediatek.camera" to IosIconKey.CAMERA,

        // Settings
        "com.android.settings" to IosIconKey.SETTINGS,
        "com.example" to IosIconKey.SETTINGS,

        // Calendar
        "com.google.android.calendar" to IosIconKey.CALENDAR,
        "com.android.calendar" to IosIconKey.CALENDAR,
        "com.samsung.android.calendar" to IosIconKey.CALENDAR,

        // Weather
        "com.google.android.apps.weather" to IosIconKey.WEATHER,
        "com.sec.android.daemonapp" to IosIconKey.WEATHER,
        "com.miui.weather2" to IosIconKey.WEATHER,

        // Clock
        "com.google.android.deskclock" to IosIconKey.CLOCK,
        "com.android.deskclock" to IosIconKey.CLOCK,
        "com.sec.android.app.clockpackage" to IosIconKey.CLOCK,

        // Maps
        "com.google.android.apps.maps" to IosIconKey.MAPS,
        "com.waze" to IosIconKey.MAPS,

        // App Store / Play Store
        "com.android.vending" to IosIconKey.APP_STORE,

        // Video / FaceTime / Meet
        "com.google.android.apps.tachyon" to IosIconKey.FACETIME,
        "com.google.android.apps.meetings" to IosIconKey.FACETIME,
        "us.zoom.videomeetings" to IosIconKey.FACETIME,

        // Podcasts
        "com.google.android.apps.podcasts" to IosIconKey.PODCASTS,

        // Health / Fit
        "com.google.android.apps.fitness" to IosIconKey.HEALTH,
        "com.sec.android.app.shealth" to IosIconKey.HEALTH,

        // Wallet
        "com.google.android.apps.walletnfcrel" to IosIconKey.WALLET,
        "com.samsung.android.spay" to IosIconKey.WALLET,

        // Notes
        "com.google.android.keep" to IosIconKey.NOTES,
        "com.samsung.android.app.notes" to IosIconKey.NOTES,
        "com.miui.notes" to IosIconKey.NOTES,

        // Reminders / Tasks
        "com.google.android.apps.tasks" to IosIconKey.REMINDERS,
        "com.microsoft.todos" to IosIconKey.REMINDERS,

        // TV
        "com.google.android.videos" to IosIconKey.TV,
        "com.apple.atve.androidtv.appletv" to IosIconKey.TV,

        // Files
        "com.google.android.apps.nbu.files" to IosIconKey.FILES,
        "com.android.documentsui" to IosIconKey.FILES,
        "com.google.android.documentsui" to IosIconKey.FILES,
        "com.sec.android.app.myfiles" to IosIconKey.FILES,

        // Mail
        "com.google.android.gm" to IosIconKey.MAIL,
        "com.microsoft.office.outlook" to IosIconKey.MAIL,
        "com.samsung.android.email.provider" to IosIconKey.MAIL,

        // Contacts
        "com.google.android.contacts" to IosIconKey.CONTACTS,
        "com.android.contacts" to IosIconKey.CONTACTS,
        "com.samsung.android.app.contacts" to IosIconKey.CONTACTS,

        // Calculator
        "com.google.android.calculator" to IosIconKey.CALCULATOR,
        "com.android.calculator2" to IosIconKey.CALCULATOR,
        "com.sec.android.app.popupcalculator" to IosIconKey.CALCULATOR,
        "com.miui.calculator" to IosIconKey.CALCULATOR,

        // Google
        "com.google.android.googlequicksearchbox" to IosIconKey.GOOGLE,

        // YouTube
        "com.google.android.youtube" to IosIconKey.YOUTUBE,

        // Social
        "com.whatsapp" to IosIconKey.WHATSAPP,
        "com.whatsapp.w4b" to IosIconKey.WHATSAPP,
        "com.instagram.android" to IosIconKey.INSTAGRAM,
        "com.zhiliaoapp.musically" to IosIconKey.TIKTOK,
        "com.ss.android.ugc.trill" to IosIconKey.TIKTOK,
        "com.ss.android.ugc.aweme" to IosIconKey.TIKTOK,
        "org.telegram.messenger" to IosIconKey.TELEGRAM,
        "com.facebook.katana" to IosIconKey.FACEBOOK,
        "com.twitter.android" to IosIconKey.X_TWITTER,
        "com.google.android.apps.chromecast.app" to IosIconKey.HOME
    )

    private val iconBitmapCache = LruCache<String, CachedAppIcon>(120)

    fun resolveIconKey(
        packageName: String,
        label: String,
        customOverride: String = ""
    ): IosIconKey {
        if (customOverride.isNotBlank()) {
            runCatching { IosIconKey.valueOf(customOverride) }.getOrNull()?.let { return it }
        }
        exactPackageMap[packageName]?.let { return it }

        val pkg = packageName.lowercase(Locale.ROOT)
        val name = label.lowercase(Locale.ROOT)
        return when {
            pkg.contains("dialer") || name == "phone" || name == "telepon" -> IosIconKey.PHONE
            pkg.contains("messaging") || pkg.contains(".mms") || name == "messages" || name == "pesan" -> IosIconKey.MESSAGES
            pkg.contains("whatsapp") || name.contains("whatsapp") -> IosIconKey.WHATSAPP
            pkg.contains("instagram") || name.contains("instagram") -> IosIconKey.INSTAGRAM
            pkg.contains("musically") || pkg.contains("tiktok") || name.contains("tiktok") -> IosIconKey.TIKTOK
            pkg.contains("youtube.music") -> IosIconKey.MUSIC
            pkg.contains("youtube") || name == "youtube" -> IosIconKey.YOUTUBE
            pkg.contains("chrome") || name == "chrome" -> IosIconKey.CHROME
            pkg.contains("browser") || name.contains("safari") || name.contains("browser") -> IosIconKey.SAFARI
            pkg.contains("camera") || name == "camera" || name == "kamera" -> IosIconKey.CAMERA
            pkg.contains("gallery") || pkg.contains("photos") || name == "photos" || name == "galeri" -> IosIconKey.PHOTOS
            pkg.contains("settings") || name == "settings" || name == "pengaturan" -> IosIconKey.SETTINGS
            pkg.contains("calendar") || name == "calendar" || name == "kalender" -> IosIconKey.CALENDAR
            pkg.contains("deskclock") || pkg.contains("clock") || name == "clock" || name == "jam" -> IosIconKey.CLOCK
            pkg.contains("weather") || name == "weather" || name == "cuaca" -> IosIconKey.WEATHER
            pkg.contains("maps") || name == "maps" || name == "peta" -> IosIconKey.MAPS
            pkg.contains("calculator") || name == "calculator" || name == "kalkulator" -> IosIconKey.CALCULATOR
            pkg.contains("contacts") || name == "contacts" || name == "kontak" -> IosIconKey.CONTACTS
            pkg.contains("keep") || pkg.contains("notes") || name == "notes" || name == "catatan" -> IosIconKey.NOTES
            pkg.contains("tasks") || pkg.contains("reminder") || name.contains("reminder") -> IosIconKey.REMINDERS
            pkg.contains("files") || pkg.contains("documentsui") || name == "files" -> IosIconKey.FILES
            pkg.contains("wallet") || name == "wallet" -> IosIconKey.WALLET
            pkg.contains("vending") || name.contains("play store") || name.contains("app store") -> IosIconKey.APP_STORE
            pkg.contains(".gm") || pkg.contains("mail") || name == "gmail" || name == "mail" -> IosIconKey.MAIL
            pkg.contains("spotify") -> IosIconKey.SPOTIFY
            pkg.contains("telegram") -> IosIconKey.TELEGRAM
            pkg.contains("facebook") -> IosIconKey.FACEBOOK
            pkg.contains("twitter") -> IosIconKey.X_TWITTER
            pkg.contains("googlequicksearchbox") || name == "google" -> IosIconKey.GOOGLE
            pkg.contains("music") || name == "music" || name == "musik" -> IosIconKey.MUSIC
            pkg.contains("podcast") -> IosIconKey.PODCASTS
            pkg.contains("fitness") || pkg.contains("health") -> IosIconKey.HEALTH
            else -> IosIconKey.CUSTOM_FALLBACK
        }
    }

    fun loadCachedFallbackIcon(context: Context, packageName: String): CachedAppIcon? {
        iconBitmapCache.get(packageName)?.let { return it }
        val pm = context.packageManager
        val drawable = runCatching { pm.getApplicationIcon(packageName) }.getOrNull() ?: return null
        val bitmap = drawableToBitmap(drawable, 144)
        val (topColor, bottomColor) = extractIosBackdropColors(bitmap)
        val entry = CachedAppIcon(
            bitmap = bitmap.asImageBitmap(),
            accentTop = topColor,
            accentBottom = bottomColor
        )
        iconBitmapCache.put(packageName, entry)
        return entry
    }

    private fun drawableToBitmap(drawable: Drawable, sizePx: Int): Bitmap {
        if (drawable is BitmapDrawable && drawable.bitmap != null) {
            return Bitmap.createScaledBitmap(drawable.bitmap, sizePx, sizePx, true)
        }
        val bmp = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        drawable.setBounds(0, 0, sizePx, sizePx)
        drawable.draw(canvas)
        return bmp
    }

    private fun extractIosBackdropColors(bitmap: Bitmap): Pair<Color, Color> {
        val sample = Bitmap.createScaledBitmap(bitmap, 16, 16, true)
        var rSum = 0L
        var gSum = 0L
        var bSum = 0L
        var count = 0L
        for (y in 2 until 14) {
            for (x in 2 until 14) {
                val pixel = sample.getPixel(x, y)
                val alpha = (pixel ushr 24) and 0xFF
                if (alpha < 180) continue
                val r = (pixel ushr 16) and 0xFF
                val g = (pixel ushr 8) and 0xFF
                val b = pixel and 0xFF
                if (r > 235 && g > 235 && b > 235) continue
                rSum += r
                gSum += g
                bSum += b
                count++
            }
        }
        if (count == 0L) {
            return Color(0xFFF5F5F7) to Color(0xFFE5E5EA)
        }
        val avgArgb = android.graphics.Color.rgb(
            (rSum / count).toInt(),
            (gSum / count).toInt(),
            (bSum / count).toInt()
        )
        val hsl = FloatArray(3)
        ColorUtils.colorToHSL(avgArgb, hsl)
        hsl[1] = (hsl[1] * 0.65f).coerceIn(0.15f, 0.72f)
        hsl[2] = hsl[2].coerceIn(0.78f, 0.94f)
        val topArgb = ColorUtils.HSLToColor(hsl)
        hsl[2] = (hsl[2] - 0.10f).coerceIn(0.60f, 0.85f)
        val bottomArgb = ColorUtils.HSLToColor(hsl)
        return Color(topArgb) to Color(bottomArgb)
    }

    fun clearCache() {
        iconBitmapCache.evictAll()
    }
}
