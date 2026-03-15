package com.phykawing.calendaralarm.domain.usecase

import com.phykawing.calendaralarm.data.repository.CalendarRepository
import com.phykawing.calendaralarm.domain.model.CalendarEvent
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUpcomingEventsUseCase @Inject constructor(
    private val repository: CalendarRepository
) {
    operator fun invoke(): Flow<List<CalendarEvent>> = repository.getUpcomingEvents()
}
