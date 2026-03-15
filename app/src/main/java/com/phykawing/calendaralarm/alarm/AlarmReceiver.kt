package com.phykawing.calendaralarm.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import com.phykawing.calendaralarm.ui.alarm.AlarmActivity
import com.phykawing.calendaralarm.util.Constants

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val eventId = intent.getLongExtra(Constants.EXTRA_EVENT_ID, -1)
        val eventTitle = intent.getStringExtra(Constants.EXTRA_EVENT_TITLE) ?: "Calendar Event"
        val alarmType = intent.getStringExtra(Constants.EXTRA_ALARM_TYPE) ?: "SOUND"
        val soundUri = intent.getStringExtra(Constants.EXTRA_SOUND_URI)

        if (eventId == -1L) return

        // Acquire a wake lock to ensure the device wakes up
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(
            PowerManager.FULL_WAKE_LOCK or
            PowerManager.ACQUIRE_CAUSES_WAKEUP or
            PowerManager.ON_AFTER_RELEASE,
            "CalendarAlarm:AlarmWakeLock"
        )
        wakeLock.acquire(10_000L) // 10 seconds max

        // Start the foreground service for sound/vibration FIRST
        val serviceIntent = Intent(context, AlarmService::class.java).apply {
            putExtra(Constants.EXTRA_EVENT_ID, eventId)
            putExtra(Constants.EXTRA_EVENT_TITLE, eventTitle)
            putExtra(Constants.EXTRA_ALARM_TYPE, alarmType)
            putExtra(Constants.EXTRA_SOUND_URI, soundUri)
        }
        context.startForegroundService(serviceIntent)

        // Then launch the alarm activity from the receiver
        val activityIntent = Intent(context, AlarmActivity::class.java).apply {
            addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            )
            putExtra(Constants.EXTRA_EVENT_ID, eventId)
            putExtra(Constants.EXTRA_EVENT_TITLE, eventTitle)
        }
        context.startActivity(activityIntent)
    }
}
