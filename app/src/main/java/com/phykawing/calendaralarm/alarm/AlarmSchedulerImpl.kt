package com.phykawing.calendaralarm.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.phykawing.calendaralarm.domain.model.AlarmSetting
import com.phykawing.calendaralarm.domain.model.CalendarEvent
import com.phykawing.calendaralarm.util.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AlarmSchedulerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : AlarmScheduler {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    private fun canScheduleExactAlarms(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }
    }

    override fun schedule(event: CalendarEvent, setting: AlarmSetting) {
        val triggerTime = event.startTime - setting.offsetMillis
        if (triggerTime <= System.currentTimeMillis()) return
        if (!canScheduleExactAlarms()) return

        val pendingIntent = createPendingIntent(event, setting)

        alarmManager.setAlarmClock(
            AlarmManager.AlarmClockInfo(triggerTime, pendingIntent),
            pendingIntent
        )
    }

    override fun cancel(eventId: Long) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(Constants.EXTRA_EVENT_ID, eventId)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            eventId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    override fun scheduleSnooze(event: CalendarEvent, setting: AlarmSetting) {
        if (!canScheduleExactAlarms()) return

        val triggerTime = System.currentTimeMillis() +
            setting.snoozeDurationMinutes * 60_000L

        val pendingIntent = createPendingIntent(event, setting)

        alarmManager.setAlarmClock(
            AlarmManager.AlarmClockInfo(triggerTime, pendingIntent),
            pendingIntent
        )
    }

    private fun createPendingIntent(event: CalendarEvent, setting: AlarmSetting): PendingIntent {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(Constants.EXTRA_EVENT_ID, event.id)
            putExtra(Constants.EXTRA_EVENT_TITLE, event.title)
            putExtra(Constants.EXTRA_ALARM_TYPE, setting.alarmType.name)
            putExtra(Constants.EXTRA_SOUND_URI, setting.soundUri)
        }
        return PendingIntent.getBroadcast(
            context,
            event.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
