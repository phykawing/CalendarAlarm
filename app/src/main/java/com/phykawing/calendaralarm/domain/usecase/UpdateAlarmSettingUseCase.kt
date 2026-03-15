package com.phykawing.calendaralarm.domain.usecase

import com.phykawing.calendaralarm.alarm.AlarmScheduler
import com.phykawing.calendaralarm.data.repository.CalendarRepository
import com.phykawing.calendaralarm.domain.model.AlarmSetting
import javax.inject.Inject

class UpdateAlarmSettingUseCase @Inject constructor(
    private val repository: CalendarRepository,
    private val alarmScheduler: AlarmScheduler
) {
    suspend operator fun invoke(setting: AlarmSetting) {
        repository.saveAlarmSetting(setting)

        if (setting.isActive) {
            val event = repository.getEventById(setting.eventId) ?: return
            alarmScheduler.schedule(event, setting)
        } else {
            alarmScheduler.cancel(setting.eventId)
        }
    }
}
