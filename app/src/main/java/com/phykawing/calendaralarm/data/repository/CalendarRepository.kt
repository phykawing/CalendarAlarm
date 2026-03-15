package com.phykawing.calendaralarm.data.repository

import com.phykawing.calendaralarm.domain.model.AlarmSetting
import com.phykawing.calendaralarm.domain.model.CalendarEvent
import kotlinx.coroutines.flow.Flow

interface CalendarRepository {
    fun getUpcomingEvents(): Flow<List<CalendarEvent>>
    suspend fun getEventById(eventId: Long): CalendarEvent?
    suspend fun syncCalendar()
    suspend fun getAlarmSetting(eventId: Long): AlarmSetting?
    fun observeAlarmSetting(eventId: Long): Flow<AlarmSetting?>
    suspend fun saveAlarmSetting(setting: AlarmSetting)
    suspend fun deleteAlarmSetting(eventId: Long)
    suspend fun getActiveAlarms(): List<Pair<CalendarEvent, AlarmSetting>>
    suspend fun setAlarmEnabled(eventId: Long, enabled: Boolean)
    suspend fun setSnoozedUntil(eventId: Long, snoozedUntil: Long?)
    suspend fun applyGlobalSettingsToAll()
}
