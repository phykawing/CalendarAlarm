package com.phykawing.calendaralarm.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.phykawing.calendaralarm.data.local.entity.CalendarEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CalendarEventDao {

    @Query("SELECT * FROM calendar_events WHERE startTime > :now ORDER BY startTime ASC")
    fun getUpcomingEvents(now: Long): Flow<List<CalendarEventEntity>>

    @Query("SELECT * FROM calendar_events WHERE id = :eventId")
    suspend fun getEventById(eventId: Long): CalendarEventEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(events: List<CalendarEventEntity>)

    @Update
    suspend fun updateEvent(event: CalendarEventEntity)

    @Query("DELETE FROM calendar_events WHERE startTime < :cutoff")
    suspend fun deletePastEvents(cutoff: Long)

    @Query("SELECT id FROM calendar_events")
    suspend fun getAllEventIds(): List<Long>

    @Query("DELETE FROM calendar_events WHERE calendarId IN (:calendarIds)")
    suspend fun deleteEventsByCalendarIds(calendarIds: Set<Long>)

    @Query("DELETE FROM calendar_events")
    suspend fun deleteAll()

    @Query("UPDATE calendar_events SET snoozedUntil = :snoozedUntil WHERE id = :eventId")
    suspend fun setSnoozedUntil(eventId: Long, snoozedUntil: Long?)
}
