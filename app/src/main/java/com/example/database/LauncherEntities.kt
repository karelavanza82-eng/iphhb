package com.example.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "home_items")
data class HomeItemEntity(
    @PrimaryKey val id: String,
    val itemType: String, // "APP", "SHORTCUT", "WIDGET"
    val packageName: String,
    val activityName: String,
    val label: String,
    val pageIndex: Int, // -1 = Dock, 0..N = Home pages
    val gridRow: Int,
    val gridCol: Int,
    val spanRows: Int = 1,
    val spanCols: Int = 1,
    val widgetKind: String = "", // "WEATHER", "CALENDAR", "CLOCK", "BATTERY", "PHOTO", "MUSIC", "ANDROID_HOST"
    val appWidgetId: Int = -1,
    val widgetConfigUri: String = "",
    val customIosIconKey: String = ""
)

@Entity(tableName = "badge_prefs")
data class AppBadgePrefEntity(
    @PrimaryKey val packageName: String,
    val enabled: Boolean = true
)

@Entity(tableName = "recent_searches")
data class RecentSearchEntity(
    @PrimaryKey val query: String,
    val timestamp: Long = System.currentTimeMillis()
)
