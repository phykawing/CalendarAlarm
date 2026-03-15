package com.phykawing.calendaralarm.data.repository

import com.phykawing.calendaralarm.data.calendar.DeviceCalendarDataSource
import com.phykawing.calendaralarm.data.local.dao.AlarmSettingDao
import com.phykawing.calendaralarm.data.local.dao.CalendarEventDao
import com.phykawing.calendaralarm.data.local.entity.AlarmSettingEntity
import com.phykawing.calendaralarm.data.local.entity.CalendarEventEntity
import com.phykawing.calendaralarm.data.preferences.UserPreferencesRepository
import com.phykawing.calendaralarm.domain.model.AlarmSetting
import com.phykawing.calendaralarm.domain.model.CalendarEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CalendarRepositoryImpl @Inject constructor(
    private val calendarDataSource: DeviceCalendarDataSource,
    private val eventDao: CalendarEventDao,
    private val alarmSettingDao: AlarmSettingDao,
    private val preferencesRepository: UserPreferencesRepository
) : CalendarRepository {

    override fun getUpcomingEvents(): Flow<List<CalendarEvent>> {
        return eventDao.getUpcomingEvents(System.currentTimeMillis()).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getEventById(eventId: Long): CalendarEvent? {
        return eventDao.getEventById(eventId)?.toDomain()
    }

    override suspend fun syncCalendar() {
        val prefs = preferencesRepository.getPreferences()
        val holidayCalendarIds = calendarDataSource.getHolidayCalendarIds()
        val deviceEvents = calendarDataSource.queryUpcomingEvents(excludeCalendarIds = holidayCalendarIds)

        // Remember previous state before replacing
        val previousState = eventDao.getAllEventIds().mapNotNull { id ->
            eventDao.getEventById(id)?.let { entity ->
                id to entity
            }
        }.toMap()

        // Save all alarm settings BEFORE deleteAll() wipes them via CASCADE
        val savedAlarmSettings = deviceEvents.mapNotNull { event ->
            alarmSettingDao.getAlarmSetting(event.id)
        }.associateBy { it.eventId }

        // Replace all events with fresh data from device
        eventDao.deleteAll()

        // Restore saved alarm settings (they were CASCADE-deleted)
        // We must insert events first, then restore settings
        val deviceEventIds = deviceEvents.map { it.id }.toSet()

        // Detect changes and determine which dismissed alarms to re-enable
        val reactivatedIds = mutableSetOf<Long>()
        for (event in deviceEvents) {
            val previous = previousState[event.id]
            val alarmSetting = savedAlarmSettings[event.id]

            if (previous != null && alarmSetting != null && !alarmSetting.isActive) {
                val eventChanged = previous.title != event.title ||
                    previous.startTime != event.startTime ||
                    previous.endTime != event.endTime ||
                    previous.location != event.location ||
                    previous.description != event.description

                if (eventChanged && event.startTime > System.currentTimeMillis()) {
                    reactivatedIds.add(event.id)
                }
            }
        }

        // Build event entities with correct alarm state
        val entities = deviceEvents.map { event ->
            val previous = previousState[event.id]
            val isNewEvent = previous == null
            val savedSetting = savedAlarmSettings[event.id]
            val alarmEnabled = when {
                isNewEvent -> prefs.globalAlarmEnabled
                event.id in reactivatedIds -> true
                savedSetting != null -> savedSetting.isActive
                else -> false
            }
            val snoozedUntil = previous?.snoozedUntil
            CalendarEventEntity.fromDomain(
                event.copy(isAlarmEnabled = alarmEnabled, snoozedUntil = snoozedUntil)
            )
        }

        if (entities.isNotEmpty()) {
            eventDao.insertEvents(entities)
        }

        // Restore alarm settings now that parent events exist again
        for ((eventId, setting) in savedAlarmSettings) {
            if (eventId in deviceEventIds) {
                val restoredSetting = if (eventId in reactivatedIds) {
                    setting.copy(isActive = true)
                } else {
                    setting
                }
                alarmSettingDao.insertOrUpdate(restoredSetting)
            }
        }

        // Auto-create alarm settings only for brand new events when global alarm is enabled
        if (prefs.globalAlarmEnabled) {
            for (event in deviceEvents) {
                val isNewEvent = event.id !in previousState
                if (isNewEvent) {
                    val setting = AlarmSetting(
                        eventId = event.id,
                        offsetValue = prefs.defaultOffsetValue,
                        offsetUnit = prefs.defaultOffsetUnit,
                        alarmType = prefs.defaultAlarmType,
                        soundUri = null,
                        snoozeDurationMinutes = prefs.defaultSnoozeDuration,
                        isActive = true
                    )
                    alarmSettingDao.insertOrUpdate(AlarmSettingEntity.fromDomain(setting))
                    eventDao.getEventById(event.id)?.let { entity ->
                        eventDao.updateEvent(entity.copy(isAlarmEnabled = true))
                    }
                }
            }
        }
    }

    override suspend fun getAlarmSetting(eventId: Long): AlarmSetting? {
        return alarmSettingDao.getAlarmSetting(eventId)?.toDomain()
    }

    override fun observeAlarmSetting(eventId: Long): Flow<AlarmSetting?> {
        return alarmSettingDao.observeAlarmSetting(eventId).map { it?.toDomain() }
    }

    override suspend fun saveAlarmSetting(setting: AlarmSetting) {
        alarmSettingDao.insertOrUpdate(AlarmSettingEntity.fromDomain(setting))
        setAlarmEnabled(setting.eventId, setting.isActive)
    }

    override suspend fun deleteAlarmSetting(eventId: Long) {
        alarmSettingDao.delete(eventId)
        setAlarmEnabled(eventId, false)
    }

    override suspend fun getActiveAlarms(): List<Pair<CalendarEvent, AlarmSetting>> {
        return alarmSettingDao.getActiveAlarms().mapNotNull { alarmEntity ->
            val event = eventDao.getEventById(alarmEntity.eventId)
            if (event != null) {
                event.toDomain() to alarmEntity.toDomain()
            } else null
        }
    }

    override suspend fun setAlarmEnabled(eventId: Long, enabled: Boolean) {
        val event = eventDao.getEventById(eventId) ?: return
        eventDao.updateEvent(event.copy(isAlarmEnabled = enabled))
    }

    override suspend fun setSnoozedUntil(eventId: Long, snoozedUntil: Long?) {
        eventDao.setSnoozedUntil(eventId, snoozedUntil)
    }

    override suspend fun applyGlobalSettingsToAll() {
        val prefs = preferencesRepository.getPreferences()
        val allEventIds = eventDao.getAllEventIds()

        for (eventId in allEventIds) {
            val existing = alarmSettingDao.getAlarmSetting(eventId)
            val setting = AlarmSettingEntity(
                eventId = eventId,
                offsetValue = prefs.defaultOffsetValue,
                offsetUnit = prefs.defaultOffsetUnit.name,
                alarmType = prefs.defaultAlarmType.name,
                soundUri = existing?.soundUri,
                snoozeDurationMinutes = prefs.defaultSnoozeDuration,
                isActive = prefs.globalAlarmEnabled
            )
            alarmSettingDao.insertOrUpdate(setting)
            setAlarmEnabled(eventId, prefs.globalAlarmEnabled)
        }
    }
}
