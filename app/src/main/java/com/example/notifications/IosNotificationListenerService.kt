package com.example.notifications

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

class IosNotificationListenerService : NotificationListenerService() {

    override fun onListenerConnected() {
        super.onListenerConnected()
        NotificationBadgeRepository.setListenerConnected(true)
        refreshActiveNotifications()
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        NotificationBadgeRepository.setListenerConnected(false)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        refreshActiveNotifications()
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
        refreshActiveNotifications()
    }

    private fun refreshActiveNotifications() {
        val active = runCatching { activeNotifications }.getOrNull() ?: return
        val grouped = HashMap<String, Int>()
        for (sbn in active) {
            val pkg = sbn.packageName ?: continue
            if (pkg == packageName) continue
            val isGroupSummary = (sbn.notification.flags and Notification.FLAG_GROUP_SUMMARY) != 0
            if (isGroupSummary) continue
            val number = sbn.notification.number
            val increment = if (number > 1) number else 1
            grouped[pkg] = (grouped[pkg] ?: 0) + increment
        }
        NotificationBadgeRepository.updateCounts(grouped)
    }
}
