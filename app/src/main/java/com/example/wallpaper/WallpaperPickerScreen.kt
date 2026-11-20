package com.example.wallpaper

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.database.LauncherSettings
import com.example.database.WallpaperPreset
import com.example.ui.theme.IosBlue
import com.example.ui.theme.IosSansFontFamily
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun WallpaperPickerScreen(
    settings: LauncherSettings,
    wallpaperRepository: WallpaperRepository,
    onApplyWallpaper: (
        preset: WallpaperPreset,
        customPath: String,
        scale: Float,
        offsetX: Float,
        offsetY: Float
    ) -> Unit,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var selectedPreset by remember(settings.wallpaperPreset) { mutableStateOf(settings.wallpaperPreset) }
    var customPath by remember(settings.customWallpaperPath) { mutableStateOf(settings.customWallpaperPath) }
    var scale by remember(settings.wallpaperScale) { mutableFloatStateOf(settings.wallpaperScale) }
    var offsetX by remember(settings.wallpaperOffsetX) { mutableFloatStateOf(settings.wallpaperOffsetX) }
    var offsetY by remember(settings.wallpaperOffsetY) { mutableFloatStateOf(settings.wallpaperOffsetY) }
    var alsoSetSystemWallpaper by remember { mutableStateOf(false) }

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                val savedPath = wallpaperRepository.saveCustomWallpaperFromUri(uri)
                if (savedPath != null) {
                    customPath = savedPath
                    selectedPreset = WallpaperPreset.CUSTOM_FILE
                    scale = 1f
                    offsetX = 0f
                    offsetY = 0f
                } else {
                    Toast.makeText(context, "Could not load selected image", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val presets = remember {
        listOf(
            WallpaperPreset.BUILTIN_DARK to R.drawable.img_wallpaper_ios_dark,
            WallpaperPreset.BUILTIN_VIOLET to R.drawable.img_wallpaper_ios_violet,
            WallpaperPreset.BUILTIN_AZURE to R.drawable.img_wallpaper_ios_azure
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0E0E10))
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("wallpaper_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Text(
                text = "Wallpaper & Crop",
                color = Color.White,
                fontFamily = IosSansFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp
            )
            Button(
                onClick = {
                    scope.launch {
                        onApplyWallpaper(selectedPreset, customPath, scale, offsetX, offsetY)
                        if (alsoSetSystemWallpaper) {
                            wallpaperRepository.applyToSystemWallpaper(selectedPreset, customPath)
                        }
                        Toast.makeText(context, "Wallpaper applied", Toast.LENGTH_SHORT).show()
                        onBack()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = IosBlue),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                modifier = Modifier.testTag("apply_wallpaper_button")
            ) {
                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Set", fontFamily = IosSansFontFamily, fontWeight = FontWeight.SemiBold)
            }
        }

        // Live interactive iPhone preview with pinch-zoom & pan crop
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 42.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .aspectRatio(9f / 19.5f)
                    .clip(RoundedCornerShape(36.dp))
                    .border(2.dp, Color.White.copy(alpha = 0.28f), RoundedCornerShape(36.dp))
                    .pointerInput(selectedPreset, customPath) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            scale = (scale * zoom).coerceIn(1f, 3f)
                            val maxOffset = 220f * scale
                            offsetX = (offsetX + pan.x).coerceIn(-maxOffset, maxOffset)
                            offsetY = (offsetY + pan.y).coerceIn(-maxOffset, maxOffset)
                        }
                    }
            ) {
                val imageModifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        translationX = offsetX
                        translationY = offsetY
                    }

                if (selectedPreset == WallpaperPreset.CUSTOM_FILE && customPath.isNotBlank() && File(customPath).exists()) {
                    AsyncImage(
                        model = File(customPath),
                        contentDescription = "Wallpaper preview",
                        contentScale = ContentScale.Crop,
                        modifier = imageModifier
                    )
                } else {
                    Image(
                        painter = painterResource(id = wallpaperRepository.getDrawableResForPreset(selectedPreset)),
                        contentDescription = "Wallpaper preview",
                        contentScale = ContentScale.Crop,
                        modifier = imageModifier
                    )
                }

                // Subtle preview overlay hints (pinch & drag + fake dock silhouette)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Pinch & drag to crop",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 11.sp,
                        fontFamily = IosSansFontFamily,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.45f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(Color.White.copy(alpha = 0.22f))
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = alsoSetSystemWallpaper,
                onCheckedChange = { alsoSetSystemWallpaper = it }
            )
            Text(
                text = "Also sync to Android system wallpaper",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 13.sp,
                fontFamily = IosSansFontFamily
            )
        }

        // Preset strip + Gallery Photo Picker
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .width(74.dp)
                        .clickable {
                            photoPicker.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                        .testTag("pick_gallery_wallpaper_button")
                ) {
                    Box(
                        modifier = Modifier
                            .size(width = 74.dp, height = 116.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF1F1F24))
                            .border(
                                width = if (selectedPreset == WallpaperPreset.CUSTOM_FILE) 2.5.dp else 1.dp,
                                color = if (selectedPreset == WallpaperPreset.CUSTOM_FILE) IosBlue else Color.White.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(16.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.PhotoLibrary,
                                contentDescription = "Pick from Gallery",
                                tint = IosBlue,
                                modifier = Modifier.size(26.dp)
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = "Photos",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontFamily = IosSansFontFamily
                            )
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                    Text("Gallery", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                }
            }

            items(presets) { (preset, drawableRes) ->
                val isSelected = selectedPreset == preset
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .width(74.dp)
                        .clickable {
                            selectedPreset = preset
                            scale = 1f
                            offsetX = 0f
                            offsetY = 0f
                        }
                        .testTag("preset_${preset.id}")
                ) {
                    Image(
                        painter = painterResource(id = drawableRes),
                        contentDescription = preset.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(width = 74.dp, height = 116.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .border(
                                width = if (isSelected) 2.5.dp else 1.dp,
                                color = if (isSelected) IosBlue else Color.White.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(16.dp)
                            )
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = preset.title,
                        color = if (isSelected) IosBlue else Color.White.copy(alpha = 0.8f),
                        fontSize = 11.sp,
                        maxLines = 1
                    )
                }
            }
        }
    }
}
