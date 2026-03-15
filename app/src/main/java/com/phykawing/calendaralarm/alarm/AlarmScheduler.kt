package com.phykawing.calendaralarm.alarm

import com.phykawing.calendaralarm.domain.model.AlarmSetting
import com.phykawing.calendaralarm.domain.model.CalendarEvent

interface AlarmScheduler {
    fun schedule(event: CalendarEvent, setting: AlarmSetting)
    fun cancel(eventId: Long)
    fun scheduleSnooze(event: CalendarEvent, setting: AlarmSetting)
}
