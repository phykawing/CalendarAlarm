package com.phykawing.calendaralarm.domain.usecase

import com.phykawing.calendaralarm.alarm.AlarmScheduler
import com.phykawing.calendaralarm.data.repository.CalendarRepository
import javax.inject.Inject

class SyncCalendarUseCase @Inject constructor(
    private val repository: CalendarRepository,
    private val alarmScheduler: AlarmScheduler
) {
    suspend operator fun invoke() {
        repository.syncCalendar()

        // Schedule alarms for all active alarm settings
        val activeAlarms = repository.getActiveAlarms()
        for ((event, setting) in activeAlarms) {
            alarmScheduler.schedule(event, setting)
        }
    }
}
