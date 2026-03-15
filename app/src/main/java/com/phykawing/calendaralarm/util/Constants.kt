package com.phykawing.calendaralarm.util

object Constants {
    const val ALARM_CHANNEL_ID = "alarm_channel"
    const val SYNC_CHANNEL_ID = "sync_channel"
    const val ALARM_NOTIFICATION_ID = 1001
    const val SYNC_NOTIFICATION_ID = 1002

    const val EXTRA_EVENT_ID = "extra_event_id"
    const val EXTRA_EVENT_TITLE = "extra_event_title"
    const val EXTRA_ALARM_TYPE = "extra_alarm_type"
    const val EXTRA_SOUND_URI = "extra_sound_uri"

    const val DEFAULT_SNOOZE_MINUTES = 5
    const val DEFAULT_OFFSET_VALUE = 15
    const val VOLUME_RAMP_DURATION_MS = 30_000L

    const val SYNC_WORK_NAME = "calendar_sync_work"
    const val SYNC_INTERVAL_MINUTES = 15L
}
