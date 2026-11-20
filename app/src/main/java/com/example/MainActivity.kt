package com.example

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.launcher.LauncherViewModel
import com.example.navigation.LauncherRoot
import com.example.ui.theme.IosLauncherTheme
import com.example.wallpaper.WallpaperRepository

class MainActivity : ComponentActivity() {

    private val appInstance: IosLauncherApp
        get() = application as IosLauncherApp

    private val launcherViewModel: LauncherViewModel by viewModels {
        LauncherViewModel.Factory(
            appContext = applicationContext,
            layoutRepository = appInstance.layoutRepository,
            settingsRepository = appInstance.settingsRepository,
            appCatalogManager = appInstance.appCatalogManager,
            widgetHostManager = appInstance.widgetHostManager,
            weatherRepository = appInstance.weatherRepository
        )
    }

    private lateinit var wallpaperRepository: WallpaperRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )
        wallpaperRepository = WallpaperRepository(applicationContext)

        // Launchers should stay on the Home screen when Back is pressed at root
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (launcherViewModel.isEditMode.value) {
                    launcherViewModel.setEditMode(false)
                }
            }
        })

        setContent {
            IosLauncherTheme {
                LauncherRoot(
                    viewModel = launcherViewModel,
                    wallpaperRepository = wallpaperRepository
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        appInstance.widgetHostManager.startListening()
    }

    override fun onResume() {
        super.onResume()
        launcherViewModel.refreshCalendar()
        launcherViewModel.triggerClosingAnimationIfNeeded()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        launcherViewModel.triggerClosingAnimationIfNeeded()
    }

    override fun onStop() {
        super.onStop()
        appInstance.widgetHostManager.stopListening()
    }
}
