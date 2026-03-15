package com.phykawing.calendaralarm.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.phykawing.calendaralarm.domain.model.AlarmType
import com.phykawing.calendaralarm.domain.model.TimeUnit
import com.phykawing.calendaralarm.util.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

data class UserPreferences(
    val globalAlarmEnabled: Boolean = false,
    val defaultOffsetValue: Int = Constants.DEFAULT_OFFSET_VALUE,
    val defaultOffsetUnit: TimeUnit = TimeUnit.MINUTES,
    val defaultAlarmType: AlarmType = AlarmType.SOUND,
    val defaultSnoozeDuration: Int = Constants.DEFAULT_SNOOZE_MINUTES,
    val isSyncEnabled: Boolean = true
)

@Singleton
class UserPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore

    private object Keys {
        val GLOBAL_ALARM_ENABLED = booleanPreferencesKey("global_alarm_enabled")
        val DEFAULT_OFFSET_VALUE = intPreferencesKey("default_offset_value")
        val DEFAULT_OFFSET_UNIT = stringPreferencesKey("default_offset_unit")
        val DEFAULT_ALARM_TYPE = stringPreferencesKey("default_alarm_type")
        val DEFAULT_SNOOZE_DURATION = intPreferencesKey("default_snooze_duration")
        val SYNC_ENABLED = booleanPreferencesKey("sync_enabled")
    }

    val preferences: Flow<UserPreferences> = dataStore.data.map { prefs ->
        UserPreferences(
            globalAlarmEnabled = prefs[Keys.GLOBAL_ALARM_ENABLED] ?: false,
            defaultOffsetValue = prefs[Keys.DEFAULT_OFFSET_VALUE] ?: Constants.DEFAULT_OFFSET_VALUE,
            defaultOffsetUnit = prefs[Keys.DEFAULT_OFFSET_UNIT]?.let { TimeUnit.valueOf(it) } ?: TimeUnit.MINUTES,
            defaultAlarmType = prefs[Keys.DEFAULT_ALARM_TYPE]?.let { AlarmType.valueOf(it) } ?: AlarmType.SOUND,
            defaultSnoozeDuration = prefs[Keys.DEFAULT_SNOOZE_DURATION] ?: Constants.DEFAULT_SNOOZE_MINUTES,
            isSyncEnabled = prefs[Keys.SYNC_ENABLED] ?: true
        )
    }

    suspend fun getPreferences(): UserPreferences = preferences.first()

    suspend fun setGlobalAlarmEnabled(enabled: Boolean) {
        dataStore.edit { it[Keys.GLOBAL_ALARM_ENABLED] = enabled }
    }

    suspend fun setDefaultOffsetValue(value: Int) {
        dataStore.edit { it[Keys.DEFAULT_OFFSET_VALUE] = value }
    }

    suspend fun setDefaultOffsetUnit(unit: TimeUnit) {
        dataStore.edit { it[Keys.DEFAULT_OFFSET_UNIT] = unit.name }
    }

    suspend fun setDefaultAlarmType(type: AlarmType) {
        dataStore.edit { it[Keys.DEFAULT_ALARM_TYPE] = type.name }
    }

    suspend fun setDefaultSnoozeDuration(minutes: Int) {
        dataStore.edit { it[Keys.DEFAULT_SNOOZE_DURATION] = minutes }
    }

    suspend fun setSyncEnabled(enabled: Boolean) {
        dataStore.edit { it[Keys.SYNC_ENABLED] = enabled }
    }
}
