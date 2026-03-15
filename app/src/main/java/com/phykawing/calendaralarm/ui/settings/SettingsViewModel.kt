package com.phykawing.calendaralarm.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phykawing.calendaralarm.data.preferences.UserPreferencesRepository
import com.phykawing.calendaralarm.data.repository.CalendarRepository
import com.phykawing.calendaralarm.domain.model.AlarmType
import com.phykawing.calendaralarm.domain.model.TimeUnit
import com.phykawing.calendaralarm.domain.usecase.SyncCalendarUseCase
import com.phykawing.calendaralarm.sync.SyncScheduler
import com.phykawing.calendaralarm.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val globalAlarmEnabled: Boolean = false,
    val defaultOffsetValue: String = Constants.DEFAULT_OFFSET_VALUE.toString(),
    val defaultOffsetUnit: TimeUnit = TimeUnit.MINUTES,
    val defaultAlarmType: AlarmType = AlarmType.SOUND,
    val defaultSnoozeDuration: Int = Constants.DEFAULT_SNOOZE_MINUTES,
    val isSyncEnabled: Boolean = true,
    val showApplyAllDialog: Boolean = false,
    val isApplying: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val syncScheduler: SyncScheduler,
    private val preferencesRepository: UserPreferencesRepository,
    private val syncCalendar: SyncCalendarUseCase,
    private val repository: CalendarRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState

    init {
        viewModelScope.launch {
            preferencesRepository.preferences.collect { prefs ->
                _uiState.update {
                    it.copy(
                        globalAlarmEnabled = prefs.globalAlarmEnabled,
                            defaultOffsetValue = prefs.defaultOffsetValue.toString(),
                        defaultOffsetUnit = prefs.defaultOffsetUnit,
                        defaultAlarmType = prefs.defaultAlarmType,
                        defaultSnoozeDuration = prefs.defaultSnoozeDuration,
                        isSyncEnabled = prefs.isSyncEnabled
                    )
                }
            }
        }
    }

    fun toggleGlobalAlarm(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setGlobalAlarmEnabled(enabled)
            // Re-sync to apply global alarm to all existing events
            syncCalendar()
        }
    }

    fun updateDefaultOffset(value: String) {
        _uiState.update { it.copy(defaultOffsetValue = value) }
        val intValue = value.toIntOrNull() ?: return
        viewModelScope.launch {
            preferencesRepository.setDefaultOffsetValue(intValue)
        }
    }

    fun updateDefaultUnit(unit: TimeUnit) {
        viewModelScope.launch {
            preferencesRepository.setDefaultOffsetUnit(unit)
        }
    }

    fun updateDefaultAlarmType(type: AlarmType) {
        viewModelScope.launch {
            preferencesRepository.setDefaultAlarmType(type)
        }
    }

    fun updateDefaultSnoozeDuration(minutes: Int) {
        viewModelScope.launch {
            preferencesRepository.setDefaultSnoozeDuration(minutes)
        }
    }

    fun toggleSync(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setSyncEnabled(enabled)
        }
        if (enabled) {
            syncScheduler.schedulePeriodicSync()
        } else {
            syncScheduler.cancelSync()
        }
    }

    fun showApplyAllDialog() {
        _uiState.update { it.copy(showApplyAllDialog = true) }
    }

    fun dismissApplyAllDialog() {
        _uiState.update { it.copy(showApplyAllDialog = false) }
    }

    fun applyGlobalSettingsToAll() {
        _uiState.update { it.copy(showApplyAllDialog = false, isApplying = true) }
        viewModelScope.launch {
            repository.applyGlobalSettingsToAll()
            syncCalendar()
            _uiState.update { it.copy(isApplying = false) }
        }
    }
}
