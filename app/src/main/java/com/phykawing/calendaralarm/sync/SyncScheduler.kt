package com.phykawing.calendaralarm.sync

import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.phykawing.calendaralarm.util.Constants
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SyncScheduler @Inject constructor(
    private val workManager: WorkManager
) {
    fun schedulePeriodicSync() {
        val request = PeriodicWorkRequestBuilder<CalendarSyncWorker>(
            Constants.SYNC_INTERVAL_MINUTES, TimeUnit.MINUTES
        ).setConstraints(
            Constraints.Builder().build()
        ).build()

        workManager.enqueueUniquePeriodicWork(
            Constants.SYNC_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    fun cancelSync() {
        workManager.cancelUniqueWork(Constants.SYNC_WORK_NAME)
    }
}
