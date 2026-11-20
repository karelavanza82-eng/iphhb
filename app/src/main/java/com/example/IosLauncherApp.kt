package com.example

import android.app.Application
import com.example.database.LauncherDatabase
import com.example.database.LauncherLayoutRepository
import com.example.database.SettingsRepository
import com.example.launcher.AppCatalogManager
import com.example.widgets.AppWidgetHostManager
import com.example.widgets.WeatherRepository

class IosLauncherApp : Application() {

    lateinit var database: LauncherDatabase
        private set
    lateinit var layoutRepository: LauncherLayoutRepository
        private set
    lateinit var settingsRepository: SettingsRepository
        private set
    lateinit var appCatalogManager: AppCatalogManager
        private set
    lateinit var widgetHostManager: AppWidgetHostManager
        private set
    lateinit var weatherRepository: WeatherRepository
        private set

    override fun onCreate() {
        super.onCreate()
        database = LauncherDatabase.getInstance(this)
        layoutRepository = LauncherLayoutRepository(database.launcherDao())
        settingsRepository = SettingsRepository(this)
        appCatalogManager = AppCatalogManager(this)
        widgetHostManager = AppWidgetHostManager(this)
        weatherRepository = WeatherRepository(this)
    }

    override fun onTerminate() {
        appCatalogManager.dispose()
        super.onTerminate()
    }
}
