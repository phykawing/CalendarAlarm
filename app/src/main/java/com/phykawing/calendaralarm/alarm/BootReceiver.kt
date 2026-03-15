package com.phykawing.calendaralarm.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.phykawing.calendaralarm.data.local.AppDatabase
import com.phykawing.calendaralarm.data.local.entity.CalendarEventEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = androidx.room.Room.databaseBuilder(
                    context.applicationContext, AppDatabase::class.java, "calendar_alarm_db"
                ).build()

                val scheduler = AlarmSchedulerImpl(context.applicationContext)
                val activeAlarms = db.alarmSettingDao().getActiveAlarms()

                for (alarmEntity in activeAlarms) {
                    val eventEntity = db.calendarEventDao().getEventById(alarmEntity.eventId)
                    if (eventEntity != null) {
                        scheduler.schedule(eventEntity.toDomain(), alarmEntity.toDomain())
                    }
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}
