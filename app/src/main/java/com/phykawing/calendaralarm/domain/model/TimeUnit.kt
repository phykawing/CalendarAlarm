package com.phykawing.calendaralarm.domain.model

enum class TimeUnit(val label: String) {
    MINUTES("min"),
    HOURS("hr");

    fun toMillis(value: Int): Long = when (this) {
        MINUTES -> value * 60_000L
        HOURS -> value * 3_600_000L
    }
}
