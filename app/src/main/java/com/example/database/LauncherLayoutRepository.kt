package com.example.database

import com.example.icons.IosIconKey
import com.example.icons.IosIconMapper
import com.example.launcher.AppCatalogManager
import com.example.launcher.InstalledAppInfo
import com.example.widgets.IosWidgetKind
import kotlinx.coroutines.flow.Flow
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class LauncherLayoutRepository(private val dao: LauncherDao) {

    val homeItemsFlow: Flow<List<HomeItemEntity>> = dao.observeHomeItems()
    val badgePrefsFlow: Flow<List<AppBadgePrefEntity>> = dao.observeBadgePrefs()
    val recentSearchesFlow: Flow<List<RecentSearchEntity>> = dao.observeRecentSearches()

    suspend fun ensureInitializedAndSynced(
        installedApps: List<InstalledAppInfo>,
        gridCols: Int = 4,
        gridRows: Int = 6
    ) {
        val current = dao.getAllHomeItemsOnce()
        if (current.isEmpty()) {
            val initial = buildInitialIosLayout(installedApps, gridCols, gridRows)
            dao.replaceAllHomeItems(initial)
            return
        }

        // Remove uninstalled third-party apps without touching user layout
        val installedSet = installedApps.map { it.packageName }.toSet()
        val removedIds = current.filter { item ->
            item.itemType == "APP" &&
                !item.packageName.startsWith("com.example.internal.") &&
                item.packageName !in installedSet
        }.map { it.id }

        removedIds.forEach { dao.deleteHomeItemById(it) }

        // Add newly installed apps that are not on home or dock yet (only if not intentionally hidden? Wait: track new apps since last sync? Actually auto-add only when package not present anywhere)
        val remaining = dao.getAllHomeItemsOnce()
        val placedPackages = remaining.filter { it.itemType == "APP" }.map { it.packageName }.toSet()
        val newApps = installedApps.filter { it.packageName !in placedPackages }
        if (newApps.isNotEmpty()) {
            val itemsToAdd = mutableListOf<HomeItemEntity>()
            val mutableGrid = remaining.toMutableList()
            for (app in newApps) {
                val slot = findNextFreeSlot(mutableGrid, startPage = 1.coerceAtMost(maxPageIndex(mutableGrid)), cols = gridCols, rows = gridRows)
                val entity = HomeItemEntity(
                    id = "app_${app.packageName}_${UUID.randomUUID().toString().take(6)}",
                    itemType = "APP",
                    packageName = app.packageName,
                    activityName = app.activityName,
                    label = app.label,
                    pageIndex = slot.first,
                    gridRow = slot.second,
                    gridCol = slot.third
                )
                mutableGrid.add(entity)
                itemsToAdd.add(entity)
            }
            dao.upsertHomeItems(itemsToAdd)
        }
    }

    suspend fun moveOrSwapItem(
        draggedId: String,
        targetPage: Int,
        targetRow: Int,
        targetCol: Int,
        gridCols: Int = 4
    ) {
        val all = dao.getAllHomeItemsOnce().toMutableList()
        val dragged = all.find { it.id == draggedId } ?: return

        if (targetPage == -1 && dragged.itemType == "WIDGET") return

        val clampedCol = targetCol.coerceIn(0, (gridCols - dragged.spanCols).coerceAtLeast(0))
        val clampedRow = if (targetPage == -1) 0 else targetRow.coerceAtLeast(0)

        val occupant = all.find { other ->
            other.id != dragged.id &&
                other.pageIndex == targetPage &&
                overlaps(
                    clampedRow, clampedCol, dragged.spanRows, dragged.spanCols,
                    other.gridRow, other.gridCol, other.spanRows, other.spanCols
                )
        }

        if (occupant != null && occupant.itemType != "WIDGET" && dragged.itemType != "WIDGET") {
            // Swap positions cleanly
            dao.upsertHomeItems(
                listOf(
                    dragged.copy(pageIndex = targetPage, gridRow = clampedRow, gridCol = clampedCol),
                    occupant.copy(pageIndex = dragged.pageIndex, gridRow = dragged.gridRow, gridCol = dragged.gridCol)
                )
            )
        } else if (occupant == null) {
            dao.upsertHomeItem(
                dragged.copy(pageIndex = targetPage, gridRow = clampedRow, gridCol = clampedCol)
            )
        }
    }

    suspend fun removeItemFromHome(id: String) {
        dao.deleteHomeItemById(id)
    }

    suspend fun addAppToHome(app: InstalledAppInfo, gridCols: Int = 4, gridRows: Int = 6) {
        val all = dao.getAllHomeItemsOnce()
        val slot = findNextFreeSlot(all, startPage = 0, cols = gridCols, rows = gridRows)
        dao.upsertHomeItem(
            HomeItemEntity(
                id = "app_${app.packageName}_${UUID.randomUUID().toString().take(6)}",
                itemType = "APP",
                packageName = app.packageName,
                activityName = app.activityName,
                label = app.label,
                pageIndex = slot.first,
                gridRow = slot.second,
                gridCol = slot.third
            )
        )
    }

    suspend fun addWidgetToHome(
        kind: IosWidgetKind,
        spanCols: Int,
        spanRows: Int,
        appWidgetId: Int = -1,
        label: String = kind.title,
        packageName: String = "",
        gridCols: Int = 4,
        gridRows: Int = 6
    ) {
        val all = dao.getAllHomeItemsOnce()
        val slot = findNextFreeWidgetSlot(all, cols = gridCols, rows = gridRows, spanCols = spanCols, spanRows = spanRows)
        dao.upsertHomeItem(
            HomeItemEntity(
                id = "widget_${kind.name}_${UUID.randomUUID().toString().take(8)}",
                itemType = "WIDGET",
                packageName = packageName,
                activityName = "",
                label = label,
                pageIndex = slot.first,
                gridRow = slot.second,
                gridCol = slot.third,
                spanRows = spanRows,
                spanCols = spanCols,
                widgetKind = kind.name,
                appWidgetId = appWidgetId
            )
        )
    }

    suspend fun updateWidgetNote(id: String, noteText: String) {
        val item = dao.getAllHomeItemsOnce().find { it.id == id } ?: return
        dao.upsertHomeItem(item.copy(label = noteText))
    }

    suspend fun updateCustomIconOverride(id: String, iconKey: String) {
        val item = dao.getAllHomeItemsOnce().find { it.id == id } ?: return
        dao.upsertHomeItem(item.copy(customIosIconKey = iconKey))
    }

    suspend fun resetLayout(installedApps: List<InstalledAppInfo>, gridCols: Int = 4, gridRows: Int = 6) {
        val initial = buildInitialIosLayout(installedApps, gridCols, gridRows)
        dao.replaceAllHomeItems(initial)
    }

    suspend fun setBadgeEnabledForPackage(packageName: String, enabled: Boolean) {
        dao.upsertBadgePref(AppBadgePrefEntity(packageName = packageName, enabled = enabled))
    }

    suspend fun addRecentSearch(query: String) {
        val trimmed = query.trim()
        if (trimmed.isNotEmpty()) {
            dao.upsertRecentSearch(RecentSearchEntity(query = trimmed, timestamp = System.currentTimeMillis()))
        }
    }

    suspend fun clearRecentSearches() {
        dao.clearRecentSearches()
    }

    suspend fun exportBackupJson(): String {
        val items = dao.getAllHomeItemsOnce()
        val arr = JSONArray()
        items.forEach { item ->
            arr.put(
                JSONObject().apply {
                    put("id", item.id)
                    put("itemType", item.itemType)
                    put("packageName", item.packageName)
                    put("activityName", item.activityName)
                    put("label", item.label)
                    put("pageIndex", item.pageIndex)
                    put("gridRow", item.gridRow)
                    put("gridCol", item.gridCol)
                    put("spanRows", item.spanRows)
                    put("spanCols", item.spanCols)
                    put("widgetKind", item.widgetKind)
                    put("customIosIconKey", item.customIosIconKey)
                }
            )
        }
        return JSONObject().apply {
            put("version", 1)
            put("items", arr)
        }.toString(2)
    }

    suspend fun importBackupJson(jsonString: String): Boolean {
        return runCatching {
            val root = JSONObject(jsonString)
            val arr = root.getJSONArray("items")
            val parsed = buildList {
                for (i in 0 until arr.length()) {
                    val o = arr.getJSONObject(i)
                    add(
                        HomeItemEntity(
                            id = o.getString("id"),
                            itemType = o.getString("itemType"),
                            packageName = o.optString("packageName"),
                            activityName = o.optString("activityName"),
                            label = o.optString("label"),
                            pageIndex = o.getInt("pageIndex"),
                            gridRow = o.getInt("gridRow"),
                            gridCol = o.getInt("gridCol"),
                            spanRows = o.optInt("spanRows", 1),
                            spanCols = o.optInt("spanCols", 1),
                            widgetKind = o.optString("widgetKind"),
                            customIosIconKey = o.optString("customIosIconKey")
                        )
                    )
                }
            }
            if (parsed.isNotEmpty()) {
                dao.replaceAllHomeItems(parsed)
                true
            } else false
        }.getOrDefault(false)
    }

    private fun buildInitialIosLayout(
        installedApps: List<InstalledAppInfo>,
        gridCols: Int,
        gridRows: Int
    ): List<HomeItemEntity> {
        val result = mutableListOf<HomeItemEntity>()

        // Page 0 Top Widgets matching photo reference: Weather (2x2) + Calendar (2x2)
        result.add(
            HomeItemEntity(
                id = "widget_weather_default",
                itemType = "WIDGET",
                packageName = AppCatalogManager.INTERNAL_WEATHER_PKG,
                activityName = "",
                label = "Weather",
                pageIndex = 0,
                gridRow = 0,
                gridCol = 0,
                spanRows = 2,
                spanCols = 2,
                widgetKind = IosWidgetKind.WEATHER.name
            )
        )
        result.add(
            HomeItemEntity(
                id = "widget_calendar_default",
                itemType = "WIDGET",
                packageName = "",
                activityName = "",
                label = "Calendar",
                pageIndex = 0,
                gridRow = 0,
                gridCol = 2,
                spanRows = 2,
                spanCols = 2,
                widgetKind = IosWidgetKind.CALENDAR.name
            )
        )

        val remainingApps = installedApps.toMutableList()
        fun takeMatching(vararg keys: IosIconKey): InstalledAppInfo? {
            val match = remainingApps.firstOrNull { app ->
                val resolved = IosIconMapper.resolveIconKey(app.packageName, app.label)
                resolved in keys
            }
            if (match != null) remainingApps.remove(match)
            return match
        }

        // Dock (4 favorite apps: Contacts/Phone, Safari/Chrome, Messages, Music)
        val dockCandidates = listOfNotNull(
            takeMatching(IosIconKey.PHONE, IosIconKey.CONTACTS),
            takeMatching(IosIconKey.SAFARI, IosIconKey.CHROME),
            takeMatching(IosIconKey.MESSAGES, IosIconKey.WHATSAPP),
            takeMatching(IosIconKey.MUSIC, IosIconKey.SPOTIFY)
        )
        dockCandidates.forEachIndexed { col, app ->
            result.add(
                HomeItemEntity(
                    id = "dock_${app.packageName}",
                    itemType = "APP",
                    packageName = app.packageName,
                    activityName = app.activityName,
                    label = app.label,
                    pageIndex = -1,
                    gridRow = 0,
                    gridCol = col
                )
            )
        }
        // Fill remaining dock slots if device has fewer matching apps
        var dockCol = dockCandidates.size
        while (dockCol < 4 && remainingApps.isNotEmpty()) {
            val app = remainingApps.removeAt(0)
            result.add(
                HomeItemEntity(
                    id = "dock_${app.packageName}",
                    itemType = "APP",
                    packageName = app.packageName,
                    activityName = app.activityName,
                    label = app.label,
                    pageIndex = -1,
                    gridRow = 0,
                    gridCol = dockCol
                )
            )
            dockCol++
        }

        // Prioritize iconic iPhone apps on Page 0 rows 2..5
        val priorityOrder = listOf(
            IosIconKey.FACETIME,
            IosIconKey.CALENDAR,
            IosIconKey.PHOTOS,
            IosIconKey.CAMERA,
            IosIconKey.PODCASTS,
            IosIconKey.HOME,
            IosIconKey.NOTES,
            IosIconKey.CLOCK,
            IosIconKey.REMINDERS,
            IosIconKey.TV,
            IosIconKey.APP_STORE,
            IosIconKey.GOOGLE,
            IosIconKey.MAPS,
            IosIconKey.HEALTH,
            IosIconKey.WALLET,
            IosIconKey.YOUTUBE,
            IosIconKey.WHATSAPP,
            IosIconKey.INSTAGRAM,
            IosIconKey.TIKTOK,
            IosIconKey.CALCULATOR,
            IosIconKey.MAIL,
            IosIconKey.FILES
        )

        val prioritizedApps = mutableListOf<InstalledAppInfo>()
        for (key in priorityOrder) {
            takeMatching(key)?.let { prioritizedApps.add(it) }
        }
        prioritizedApps.addAll(remainingApps)

        // Include Launcher Settings icon cleanly on Page 0 so user can customize anytime
        val launcherSettingsItem = HomeItemEntity(
            id = "internal_launcher_settings",
            itemType = "SHORTCUT",
            packageName = AppCatalogManager.INTERNAL_LAUNCHER_SETTINGS_PKG,
            activityName = "",
            label = "Launcher",
            pageIndex = 0,
            gridRow = 5,
            gridCol = 3,
            customIosIconKey = IosIconKey.SETTINGS.name
        )
        result.add(launcherSettingsItem)

        for (app in prioritizedApps) {
            val slot = findNextFreeSlot(result, startPage = 0, cols = gridCols, rows = gridRows)
            result.add(
                HomeItemEntity(
                    id = "app_${app.packageName}_${app.activityName.hashCode()}",
                    itemType = "APP",
                    packageName = app.packageName,
                    activityName = app.activityName,
                    label = app.label,
                    pageIndex = slot.first,
                    gridRow = slot.second,
                    gridCol = slot.third
                )
            )
        }
        return result
    }

    private fun maxPageIndex(items: List<HomeItemEntity>): Int {
        return items.filter { it.pageIndex >= 0 }.maxOfOrNull { it.pageIndex } ?: 0
    }

    private fun findNextFreeSlot(
        existing: List<HomeItemEntity>,
        startPage: Int,
        cols: Int,
        rows: Int
    ): Triple<Int, Int, Int> {
        var page = startPage.coerceAtLeast(0)
        while (true) {
            val pageItems = existing.filter { it.pageIndex == page }
            for (r in 0 until rows) {
                for (c in 0 until cols) {
                    val occupied = pageItems.any { item ->
                        overlaps(r, c, 1, 1, item.gridRow, item.gridCol, item.spanRows, item.spanCols)
                    }
                    if (!occupied) return Triple(page, r, c)
                }
            }
            page++
        }
    }

    private fun findNextFreeWidgetSlot(
        existing: List<HomeItemEntity>,
        cols: Int,
        rows: Int,
        spanCols: Int,
        spanRows: Int
    ): Triple<Int, Int, Int> {
        var page = 0
        while (true) {
            val pageItems = existing.filter { it.pageIndex == page }
            for (r in 0..(rows - spanRows).coerceAtLeast(0)) {
                for (c in 0..(cols - spanCols).coerceAtLeast(0)) {
                    val occupied = pageItems.any { item ->
                        overlaps(r, c, spanRows, spanCols, item.gridRow, item.gridCol, item.spanRows, item.spanCols)
                    }
                    if (!occupied) return Triple(page, r, c)
                }
            }
            page++
        }
    }

    private fun overlaps(
        r1: Int, c1: Int, sr1: Int, sc1: Int,
        r2: Int, c2: Int, sr2: Int, sc2: Int
    ): Boolean {
        val rOverlap = r1 < r2 + sr2 && r1 + sr1 > r2
        val cOverlap = c1 < c2 + sc2 && c1 + sc1 > c2
        return rOverlap && cOverlap
    }
}
