package com.phykawing.calendaralarm

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.phykawing.calendaralarm.util.Constants
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class CalendarAlarmApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        val alarmChannel = NotificationChannel(
            Constants.ALARM_CHANNEL_ID,
            "Calendar Alarms",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Alarm notifications for calendar events"
            setBypassDnd(true)
        }

        val syncChannel = NotificationChannel(
            Constants.SYNC_CHANNEL_ID,
            "Calendar Sync",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Background calendar synchronization"
        }

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(alarmChannel)
        notificationManager.createNotificationChannel(syncChannel)
    }
}
