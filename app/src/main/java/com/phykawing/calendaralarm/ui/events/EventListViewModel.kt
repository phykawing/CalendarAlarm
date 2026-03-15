package com.phykawing.calendaralarm.ui.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phykawing.calendaralarm.data.repository.CalendarRepository
import com.phykawing.calendaralarm.domain.model.CalendarEvent
import com.phykawing.calendaralarm.domain.usecase.GetUpcomingEventsUseCase
import com.phykawing.calendaralarm.domain.usecase.SyncCalendarUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EventWithAlarmTime(
    val event: CalendarEvent,
    val alarmTime: Long? = null
)

data class EventListUiState(
    val events: List<EventWithAlarmTime> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class EventListViewModel @Inject constructor(
    private val getUpcomingEvents: GetUpcomingEventsUseCase,
    private val syncCalendar: SyncCalendarUseCase,
    private val repository: CalendarRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EventListUiState())
    val uiState: StateFlow<EventListUiState> = _uiState

    init {
        refreshEvents()
        observeEvents()
    }

    @OptIn(kotlinx.coroutines.FlowPreview::class)
    private fun observeEvents() {
        viewModelScope.launch {
            getUpcomingEvents()
                .debounce(300)
                .catch { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
                .collect { events ->
                    buildUiState(events)
                }
        }
    }

    private suspend fun buildUiState(events: List<CalendarEvent>) {
        val eventsWithAlarm = events.map { event ->
            val setting = repository.getAlarmSetting(event.id)
            val alarmTime = if (event.isAlarmEnabled && setting != null) {
                event.startTime - setting.offsetMillis
            } else null
            EventWithAlarmTime(event, alarmTime)
        }
        _uiState.value = EventListUiState(
            events = eventsWithAlarm,
            isLoading = false
        )
    }

    fun refreshEvents() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                syncCalendar()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
}
