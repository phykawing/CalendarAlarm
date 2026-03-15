package com.phykawing.calendaralarm.ui.alarm

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phykawing.calendaralarm.data.repository.CalendarRepository
import com.phykawing.calendaralarm.domain.usecase.DismissAlarmUseCase
import com.phykawing.calendaralarm.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val dismissAlarmUseCase: DismissAlarmUseCase,
    private val repository: CalendarRepository
) : ViewModel() {

    val eventId: Long = savedStateHandle[Constants.EXTRA_EVENT_ID] ?: -1L
    val eventTitle: String = savedStateHandle[Constants.EXTRA_EVENT_TITLE] ?: "Calendar Event"

    private var eventStartTime: Long = Long.MAX_VALUE

    private val _snoozeMinutes = MutableStateFlow(5)
    val snoozeMinutes: StateFlow<Int> = _snoozeMinutes

    private val _maxSnoozeMinutes = MutableStateFlow(Int.MAX_VALUE)
    val maxSnoozeMinutes: StateFlow<Int> = _maxSnoozeMinutes

    init {
        viewModelScope.launch {
            val event = repository.getEventById(eventId)
            if (event != null) {
                eventStartTime = event.startTime
                updateMaxSnooze()
            }
            val setting = repository.getAlarmSetting(eventId)
            if (setting != null) {
                val clamped = setting.snoozeDurationMinutes.coerceAtMost(_maxSnoozeMinutes.value)
                _snoozeMinutes.value = clamped.coerceAtLeast(1)
            }
        }
    }

    private fun updateMaxSnooze() {
        val remainingMs = eventStartTime - System.currentTimeMillis()
        val maxMins = (remainingMs / 60_000L).toInt().coerceAtLeast(1)
        _maxSnoozeMinutes.value = maxMins
    }

    fun adjustSnooze(delta: Int) {
        updateMaxSnooze()
        val newValue = (_snoozeMinutes.value + delta)
            .coerceIn(1, _maxSnoozeMinutes.value)
        _snoozeMinutes.value = newValue
    }

    fun dismiss(onDone: () -> Unit) {
        viewModelScope.launch {
            dismissAlarmUseCase.dismiss(eventId)
            onDone()
        }
    }

    fun snooze(onDone: () -> Unit) {
        viewModelScope.launch {
            updateMaxSnooze()
            val clamped = _snoozeMinutes.value.coerceIn(1, _maxSnoozeMinutes.value)
            dismissAlarmUseCase.snooze(eventId, clamped)
            onDone()
        }
    }
}
