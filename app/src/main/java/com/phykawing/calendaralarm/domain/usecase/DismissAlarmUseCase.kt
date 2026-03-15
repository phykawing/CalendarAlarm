package com.phykawing.calendaralarm.domain.usecase

import android.content.Context
import android.content.Intent
import com.phykawing.calendaralarm.alarm.AlarmScheduler
import com.phykawing.calendaralarm.alarm.AlarmService
import com.phykawing.calendaralarm.data.repository.CalendarRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class DismissAlarmUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: CalendarRepository,
    private val alarmScheduler: AlarmScheduler
) {
    suspend fun dismiss(eventId: Long) {
        context.stopService(Intent(context, AlarmService::class.java))
        repository.setSnoozedUntil(eventId, null)
        // Deactivate alarm but keep the setting for re-enabling on event update
        val setting = repository.getAlarmSetting(eventId)
        if (setting != null) {
            repository.saveAlarmSetting(setting.copy(isActive = false))
        } else {
            repository.setAlarmEnabled(eventId, false)
        }
    }

    suspend fun snooze(eventId: Long, snoozeMinutes: Int? = null) {
        context.stopService(Intent(context, AlarmService::class.java))

        val event = repository.getEventById(eventId) ?: return
        val setting = repository.getAlarmSetting(eventId) ?: return

        val duration = snoozeMinutes ?: setting.snoozeDurationMinutes
        val snoozedUntil = System.currentTimeMillis() + duration * 60_000L
        repository.setSnoozedUntil(eventId, snoozedUntil)

        alarmScheduler.scheduleSnooze(event, setting.copy(snoozeDurationMinutes = duration))
    }
}
