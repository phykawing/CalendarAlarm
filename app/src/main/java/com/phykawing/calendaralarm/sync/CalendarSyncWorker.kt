package com.phykawing.calendaralarm.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.phykawing.calendaralarm.domain.usecase.SyncCalendarUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class CalendarSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val syncCalendar: SyncCalendarUseCase
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            syncCalendar()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
