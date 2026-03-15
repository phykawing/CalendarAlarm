package com.phykawing.calendaralarm.domain.model

data class AlarmSetting(
    val eventId: Long,
    val offsetValue: Int,
    val offsetUnit: TimeUnit,
    val alarmType: AlarmType,
    val soundUri: String?,
    val snoozeDurationMinutes: Int,
    val isActive: Boolean
) {
    val offsetMillis: Long
        get() = offsetUnit.toMillis(offsetValue)
}
