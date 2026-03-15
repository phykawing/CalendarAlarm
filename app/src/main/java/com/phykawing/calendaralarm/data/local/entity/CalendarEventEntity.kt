package com.phykawing.calendaralarm.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.phykawing.calendaralarm.domain.model.CalendarEvent

@Entity(tableName = "calendar_events")
data class CalendarEventEntity(
    @PrimaryKey val id: Long,
    val title: String,
    val description: String?,
    val startTime: Long,
    val endTime: Long,
    val location: String?,
    val calendarId: Long,
    val isAlarmEnabled: Boolean = false,
    val snoozedUntil: Long? = null
) {
    fun toDomain() = CalendarEvent(
        id = id,
        title = title,
        description = description,
        startTime = startTime,
        endTime = endTime,
        location = location,
        calendarId = calendarId,
        isAlarmEnabled = isAlarmEnabled,
        snoozedUntil = snoozedUntil
    )

    companion object {
        fun fromDomain(event: CalendarEvent) = CalendarEventEntity(
            id = event.id,
            title = event.title,
            description = event.description,
            startTime = event.startTime,
            endTime = event.endTime,
            location = event.location,
            calendarId = event.calendarId,
            isAlarmEnabled = event.isAlarmEnabled,
            snoozedUntil = event.snoozedUntil
        )
    }
}
