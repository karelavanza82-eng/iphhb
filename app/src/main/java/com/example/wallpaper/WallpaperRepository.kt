package com.example.wallpaper

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.example.R
import com.example.database.WallpaperPreset
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class WallpaperRepository(private val context: Context) {

    private val wallpaperDir: File
        get() = File(context.filesDir, "wallpapers").apply { if (!exists()) mkdirs() }

    private val customWallpaperFile: File
        get() = File(wallpaperDir, "custom_wallpaper.jpg")

    suspend fun saveCustomWallpaperFromUri(uri: Uri): String? = withContext(Dispatchers.IO) {
        runCatching {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return@withContext null
            val bytes = inputStream.use { it.readBytes() }
            val opts = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size, opts)
            val maxDim = 2400
            var sampleSize = 1
            while (opts.outWidth / sampleSize > maxDim || opts.outHeight / sampleSize > maxDim) {
                sampleSize *= 2
            }
            val decodeOpts = BitmapFactory.Options().apply { inSampleSize = sampleSize }
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, decodeOpts)
                ?: return@withContext null

            FileOutputStream(customWallpaperFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 92, out)
            }
            bitmap.recycle()
            customWallpaperFile.absolutePath
        }.getOrNull()
    }

    suspend fun applyToSystemWallpaper(preset: WallpaperPreset, customPath: String): Boolean =
        withContext(Dispatchers.IO) {
            runCatching {
                val bitmap = when (preset) {
                    WallpaperPreset.BUILTIN_DARK -> BitmapFactory.decodeResource(context.resources, R.drawable.img_wallpaper_ios_dark)
                    WallpaperPreset.BUILTIN_VIOLET -> BitmapFactory.decodeResource(context.resources, R.drawable.img_wallpaper_ios_violet)
                    WallpaperPreset.BUILTIN_AZURE -> BitmapFactory.decodeResource(context.resources, R.drawable.img_wallpaper_ios_azure)
                    WallpaperPreset.CUSTOM_FILE -> {
                        if (customPath.isNotBlank() && File(customPath).exists()) {
                            BitmapFactory.decodeFile(customPath)
                        } else null
                    }
                } ?: return@withContext false

                WallpaperManager.getInstance(context).setBitmap(
                    bitmap,
                    null,
                    true,
                    WallpaperManager.FLAG_SYSTEM
                )
                bitmap.recycle()
                true
            }.getOrDefault(false)
        }

    fun getDrawableResForPreset(preset: WallpaperPreset): Int {
        return when (preset) {
            WallpaperPreset.BUILTIN_DARK -> R.drawable.img_wallpaper_ios_dark
            WallpaperPreset.BUILTIN_VIOLET -> R.drawable.img_wallpaper_ios_violet
            WallpaperPreset.BUILTIN_AZURE -> R.drawable.img_wallpaper_ios_azure
            WallpaperPreset.CUSTOM_FILE -> R.drawable.img_wallpaper_ios_dark
        }
    }
}
