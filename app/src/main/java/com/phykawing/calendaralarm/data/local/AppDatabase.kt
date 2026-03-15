package com.phykawing.calendaralarm.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.phykawing.calendaralarm.data.local.dao.AlarmSettingDao
import com.phykawing.calendaralarm.data.local.dao.CalendarEventDao
import com.phykawing.calendaralarm.data.local.entity.AlarmSettingEntity
import com.phykawing.calendaralarm.data.local.entity.CalendarEventEntity

@Database(
    entities = [CalendarEventEntity::class, AlarmSettingEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun calendarEventDao(): CalendarEventDao
    abstract fun alarmSettingDao(): AlarmSettingDao
}
