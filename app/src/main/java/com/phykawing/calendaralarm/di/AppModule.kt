package com.phykawing.calendaralarm.di

import android.content.ContentResolver
import android.content.Context
import androidx.room.Room
import androidx.work.WorkManager
import com.phykawing.calendaralarm.data.local.AppDatabase
import com.phykawing.calendaralarm.data.local.dao.AlarmSettingDao
import com.phykawing.calendaralarm.data.local.dao.CalendarEventDao
import com.phykawing.calendaralarm.data.repository.CalendarRepository
import com.phykawing.calendaralarm.data.repository.CalendarRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "calendar_alarm_db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideCalendarEventDao(db: AppDatabase): CalendarEventDao = db.calendarEventDao()

    @Provides
    fun provideAlarmSettingDao(db: AppDatabase): AlarmSettingDao = db.alarmSettingDao()

    @Provides
    fun provideContentResolver(@ApplicationContext context: Context): ContentResolver {
        return context.contentResolver
    }

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class BindingsModule {

    @Binds
    @Singleton
    abstract fun bindCalendarRepository(impl: CalendarRepositoryImpl): CalendarRepository
}
