package com.phykawing.calendaralarm.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.phykawing.calendaralarm.data.local.entity.AlarmSettingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmSettingDao {

    @Query("SELECT * FROM alarm_settings WHERE eventId = :eventId")
    suspend fun getAlarmSetting(eventId: Long): AlarmSettingEntity?

    @Query("SELECT * FROM alarm_settings WHERE eventId = :eventId")
    fun observeAlarmSetting(eventId: Long): Flow<AlarmSettingEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(setting: AlarmSettingEntity)

    @Query("DELETE FROM alarm_settings WHERE eventId = :eventId")
    suspend fun delete(eventId: Long)

    @Query("SELECT * FROM alarm_settings WHERE isActive = 1")
    suspend fun getActiveAlarms(): List<AlarmSettingEntity>
}
