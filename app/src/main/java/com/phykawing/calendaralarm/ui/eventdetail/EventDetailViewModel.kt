package com.phykawing.calendaralarm.ui.eventdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phykawing.calendaralarm.data.repository.CalendarRepository
import com.phykawing.calendaralarm.domain.model.AlarmSetting
import com.phykawing.calendaralarm.domain.model.AlarmType
import com.phykawing.calendaralarm.domain.model.CalendarEvent
import com.phykawing.calendaralarm.domain.model.TimeUnit
import com.phykawing.calendaralarm.domain.usecase.UpdateAlarmSettingUseCase
import com.phykawing.calendaralarm.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EventDetailUiState(
    val event: CalendarEvent? = null,
    val offsetValue: String = Constants.DEFAULT_OFFSET_VALUE.toString(),
    val offsetUnit: TimeUnit = TimeUnit.MINUTES,
    val alarmType: AlarmType = AlarmType.SOUND,
    val soundUri: String? = null,
    val snoozeDuration: Int = Constants.DEFAULT_SNOOZE_MINUTES,
    val isAlarmActive: Boolean = false,
    val isLoading: Boolean = true
)

@HiltViewModel
class EventDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: CalendarRepository,
    private val updateAlarmSetting: UpdateAlarmSettingUseCase
) : ViewModel() {

    private val eventId: Long = checkNotNull(savedStateHandle["eventId"])

    private val _uiState = MutableStateFlow(EventDetailUiState())
    val uiState: StateFlow<EventDetailUiState> = _uiState

    init {
        loadEvent()
    }

    private fun loadEvent() {
        viewModelScope.launch {
            val event = repository.getEventById(eventId)
            val setting = repository.getAlarmSetting(eventId)

            _uiState.update {
                it.copy(
                    event = event,
                    offsetValue = (setting?.offsetValue ?: Constants.DEFAULT_OFFSET_VALUE).toString(),
                    offsetUnit = setting?.offsetUnit ?: TimeUnit.MINUTES,
                    alarmType = setting?.alarmType ?: AlarmType.SOUND,
                    soundUri = setting?.soundUri,
                    snoozeDuration = setting?.snoozeDurationMinutes ?: Constants.DEFAULT_SNOOZE_MINUTES,
                    isAlarmActive = setting?.isActive ?: false,
                    isLoading = false
                )
            }
        }
    }

    fun updateOffsetValue(value: String) {
        _uiState.update { it.copy(offsetValue = value) }
    }

    fun updateOffsetUnit(unit: TimeUnit) {
        _uiState.update { it.copy(offsetUnit = unit) }
    }

    fun updateAlarmType(type: AlarmType) {
        _uiState.update { it.copy(alarmType = type) }
    }

    fun updateSoundUri(uri: String?) {
        _uiState.update { it.copy(soundUri = uri) }
    }

    fun updateSnoozeDuration(minutes: Int) {
        _uiState.update { it.copy(snoozeDuration = minutes) }
    }

    fun toggleAlarm(active: Boolean) {
        _uiState.update { it.copy(isAlarmActive = active) }
        saveAlarmSetting()
    }

    fun saveAlarmSetting() {
        val state = _uiState.value
        val offsetInt = state.offsetValue.toIntOrNull() ?: return

        viewModelScope.launch {
            val setting = AlarmSetting(
                eventId = eventId,
                offsetValue = offsetInt,
                offsetUnit = state.offsetUnit,
                alarmType = state.alarmType,
                soundUri = state.soundUri,
                snoozeDurationMinutes = state.snoozeDuration,
                isActive = state.isAlarmActive
            )
            updateAlarmSetting(setting)
        }
    }
}
