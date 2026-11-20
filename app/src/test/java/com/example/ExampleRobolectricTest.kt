package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.notifications.NotificationBadgeRepository
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `app name matches iOS Launcher`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("iOS Launcher", appName)
    }

    @Test
    fun `notification badge repository updates and clears counts`() {
        NotificationBadgeRepository.updateCounts(mapOf("com.whatsapp" to 105, "com.android.settings" to 2))
        assertEquals(105, NotificationBadgeRepository.counts.value["com.whatsapp"])
        assertEquals(2, NotificationBadgeRepository.counts.value["com.android.settings"])
        NotificationBadgeRepository.setListenerConnected(false)
        assertEquals(0, NotificationBadgeRepository.counts.value.size)
    }
}
