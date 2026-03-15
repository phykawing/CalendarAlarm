package com.phykawing.calendaralarm.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.phykawing.calendaralarm.domain.model.AlarmSetting
import com.phykawing.calendaralarm.domain.model.AlarmType
import com.phykawing.calendaralarm.domain.model.TimeUnit

@Entity(
    tableName = "alarm_settings",
    foreignKeys = [
        ForeignKey(
            entity = CalendarEventEntity::class,
            parentColumns = ["id"],
            childColumns = ["eventId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class AlarmSettingEntity(
    @PrimaryKey val eventId: Long,
    val offsetValue: Int,
    val offsetUnit: String,
    val alarmType: String,
    val soundUri: String?,
    val snoozeDurationMinutes: Int,
    val isActive: Boolean
) {
    fun toDomain() = AlarmSetting(
        eventId = eventId,
        offsetValue = offsetValue,
        offsetUnit = TimeUnit.valueOf(offsetUnit),
        alarmType = AlarmType.valueOf(alarmType),
        soundUri = soundUri,
        snoozeDurationMinutes = snoozeDurationMinutes,
        isActive = isActive
    )

    companion object {
        fun fromDomain(setting: AlarmSetting) = AlarmSettingEntity(
            eventId = setting.eventId,
            offsetValue = setting.offsetValue,
            offsetUnit = setting.offsetUnit.name,
            alarmType = setting.alarmType.name,
            soundUri = setting.soundUri,
            snoozeDurationMinutes = setting.snoozeDurationMinutes,
            isActive = setting.isActive
        )
    }
}
