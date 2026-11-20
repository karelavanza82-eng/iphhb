package com.example.widgets

import android.content.ContentUris
import android.content.Context
import android.provider.CalendarContract
import com.example.permissions.PermissionsManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class CalendarEventItem(
    val title: String,
    val timeLabel: String
)

sealed interface CalendarWidgetData {
    data object NeedsPermission : CalendarWidgetData
    data class Loaded(val events: List<CalendarEventItem>) : CalendarWidgetData
}

object CalendarReader {

    suspend fun readTodayEvents(context: Context): CalendarWidgetData = withContext(Dispatchers.IO) {
        if (!PermissionsManager.hasCalendarPermission(context)) {
            return@withContext CalendarWidgetData.NeedsPermission
        }
        val now = Calendar.getInstance()
        val startOfDay = (now.clone() as Calendar).apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }.timeInMillis
        val endOfDay = (now.clone() as Calendar).apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
        }.timeInMillis

        val builder = CalendarContract.Instances.CONTENT_URI.buildUpon()
        ContentUris.appendId(builder, startOfDay)
        ContentUris.appendId(builder, endOfDay)

        val projection = arrayOf(
            CalendarContract.Instances.TITLE,
            CalendarContract.Instances.BEGIN,
            CalendarContract.Instances.ALL_DAY
        )
        val timeFmt = SimpleDateFormat("HH:mm", Locale.getDefault())
        val items = mutableListOf<CalendarEventItem>()

        runCatching {
            context.contentResolver.query(
                builder.build(),
                projection,
                null,
                null,
                "${CalendarContract.Instances.BEGIN} ASC"
            )?.use { cursor ->
                val titleIdx = cursor.getColumnIndexOrThrow(CalendarContract.Instances.TITLE)
                val beginIdx = cursor.getColumnIndexOrThrow(CalendarContract.Instances.BEGIN)
                val allDayIdx = cursor.getColumnIndexOrThrow(CalendarContract.Instances.ALL_DAY)
                while (cursor.moveToNext() && items.size < 3) {
                    val title = cursor.getString(titleIdx)?.takeIf { it.isNotBlank() } ?: continue
                    val begin = cursor.getLong(beginIdx)
                    val allDay = cursor.getInt(allDayIdx) == 1
                    val label = if (allDay) "All day" else timeFmt.format(Date(begin))
                    items.add(CalendarEventItem(title = title, timeLabel = label))
                }
            }
        }
        CalendarWidgetData.Loaded(items)
    }
}
