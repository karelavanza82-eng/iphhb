package com.example.widgets

import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetHostView
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.Context
import android.content.Intent
import android.os.Bundle

class AppWidgetHostManager(private val context: Context) {

    companion object {
        const val APP_WIDGET_HOST_ID = 1024
    }

    val appWidgetManager: AppWidgetManager = AppWidgetManager.getInstance(context)
    val appWidgetHost: AppWidgetHost = AppWidgetHost(context.applicationContext, APP_WIDGET_HOST_ID)

    private var isListening = false

    fun startListening() {
        if (!isListening) {
            runCatching { appWidgetHost.startListening() }
            isListening = true
        }
    }

    fun stopListening() {
        if (isListening) {
            runCatching { appWidgetHost.stopListening() }
            isListening = false
        }
    }

    fun getInstalledWidgetProviders(): List<AppWidgetProviderInfo> {
        return runCatching { appWidgetManager.installedProviders }
            .getOrDefault(emptyList())
            .sortedBy { it.loadLabel(context.packageManager) }
    }

    fun allocateWidgetId(): Int {
        return appWidgetHost.allocateAppWidgetId()
    }

    fun bindAppWidgetIdIfAllowed(appWidgetId: Int, providerInfo: AppWidgetProviderInfo): Boolean {
        return runCatching {
            appWidgetManager.bindAppWidgetIdIfAllowed(appWidgetId, providerInfo.provider)
        }.getOrDefault(false)
    }

    fun createBindIntent(appWidgetId: Int, providerInfo: AppWidgetProviderInfo): Intent {
        return Intent(AppWidgetManager.ACTION_APPWIDGET_BIND).apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_PROVIDER, providerInfo.provider)
        }
    }

    fun deleteAppWidgetId(appWidgetId: Int) {
        if (appWidgetId != -1) {
            runCatching { appWidgetHost.deleteAppWidgetId(appWidgetId) }
        }
    }

    fun createHostView(appWidgetId: Int, widthDp: Int, heightDp: Int): AppWidgetHostView? {
        if (appWidgetId == -1) return null
        val info = appWidgetManager.getAppWidgetInfo(appWidgetId) ?: return null
        val view = appWidgetHost.createView(context, appWidgetId, info)
        view.setAppWidget(appWidgetId, info)
        val options = Bundle().apply {
            putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, widthDp)
            putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH, widthDp)
            putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, heightDp)
            putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT, heightDp)
        }
        runCatching { view.updateAppWidgetOptions(options) }
        return view
    }
}
