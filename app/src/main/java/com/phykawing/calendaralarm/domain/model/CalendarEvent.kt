package com.phykawing.calendaralarm.domain.model

data class CalendarEvent(
    val id: Long,
    val title: String,
    val description: String?,
    val startTime: Long,
    val endTime: Long,
    val location: String?,
    val calendarId: Long,
    val isAlarmEnabled: Boolean,
    val snoozedUntil: Long? = null
) {
    val isSnoozed: Boolean
        get() = snoozedUntil != null && snoozedUntil > System.currentTimeMillis()
}
