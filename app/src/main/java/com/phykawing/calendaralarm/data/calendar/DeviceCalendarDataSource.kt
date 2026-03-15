package com.phykawing.calendaralarm.data.calendar

import android.content.ContentResolver
import android.net.Uri
import android.provider.CalendarContract
import com.phykawing.calendaralarm.domain.model.CalendarEvent
import javax.inject.Inject

class DeviceCalendarDataSource @Inject constructor(
    private val contentResolver: ContentResolver
) {
    fun getHolidayCalendarIds(): Set<Long> {
        val holidayIds = mutableSetOf<Long>()

        val projection = arrayOf(
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
            CalendarContract.Calendars.OWNER_ACCOUNT
        )

        contentResolver.query(
            CalendarContract.Calendars.CONTENT_URI,
            projection,
            null,
            null,
            null
        )?.use { cursor ->
            val idIndex = cursor.getColumnIndexOrThrow(CalendarContract.Calendars._ID)
            val nameIndex = cursor.getColumnIndexOrThrow(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME)
            val ownerIndex = cursor.getColumnIndexOrThrow(CalendarContract.Calendars.OWNER_ACCOUNT)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idIndex)
                val name = cursor.getString(nameIndex) ?: ""
                val owner = cursor.getString(ownerIndex) ?: ""

                val isHoliday = owner.contains("#holiday@group", ignoreCase = true) ||
                    name.contains("holiday", ignoreCase = true)

                if (isHoliday) {
                    holidayIds.add(id)
                }
            }
        }

        return holidayIds
    }

    fun queryUpcomingEvents(excludeCalendarIds: Set<Long> = emptySet()): List<CalendarEvent> {
        val now = System.currentTimeMillis()
        val sixMonthsLater = now + 180L * 24 * 60 * 60 * 1000
        val events = mutableListOf<CalendarEvent>()

        val projection = arrayOf(
            CalendarContract.Instances._ID,
            CalendarContract.Instances.EVENT_ID,
            CalendarContract.Instances.TITLE,
            CalendarContract.Instances.DESCRIPTION,
            CalendarContract.Instances.BEGIN,
            CalendarContract.Instances.END,
            CalendarContract.Instances.EVENT_LOCATION,
            CalendarContract.Instances.CALENDAR_ID
        )

        val uri = Uri.withAppendedPath(
            CalendarContract.Instances.CONTENT_URI,
            "$now/$sixMonthsLater"
        )

        val sortOrder = "${CalendarContract.Instances.BEGIN} ASC"

        contentResolver.query(
            uri,
            projection,
            null,
            null,
            sortOrder
        )?.use { cursor ->
            val instanceIdIndex = cursor.getColumnIndexOrThrow(CalendarContract.Instances._ID)
            val titleIndex = cursor.getColumnIndexOrThrow(CalendarContract.Instances.TITLE)
            val descIndex = cursor.getColumnIndexOrThrow(CalendarContract.Instances.DESCRIPTION)
            val startIndex = cursor.getColumnIndexOrThrow(CalendarContract.Instances.BEGIN)
            val endIndex = cursor.getColumnIndexOrThrow(CalendarContract.Instances.END)
            val locationIndex = cursor.getColumnIndexOrThrow(CalendarContract.Instances.EVENT_LOCATION)
            val calIdIndex = cursor.getColumnIndexOrThrow(CalendarContract.Instances.CALENDAR_ID)

            while (cursor.moveToNext()) {
                val calendarId = cursor.getLong(calIdIndex)
                if (calendarId in excludeCalendarIds) continue

                events.add(
                    CalendarEvent(
                        id = cursor.getLong(instanceIdIndex),
                        title = cursor.getString(titleIndex) ?: "(No title)",
                        description = cursor.getString(descIndex),
                        startTime = cursor.getLong(startIndex),
                        endTime = cursor.getLong(endIndex),
                        location = cursor.getString(locationIndex),
                        calendarId = calendarId,
                        isAlarmEnabled = false
                    )
                )
            }
        }

        return events
    }
}
