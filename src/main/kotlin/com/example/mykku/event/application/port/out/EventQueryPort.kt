package com.example.mykku.event.application.port.out

import com.example.mykku.event.domain.Event
import com.example.mykku.event.domain.EventImage
import com.example.mykku.event.domain.EventSortType
import com.example.mykku.event.domain.EventStatusType
import com.example.mykku.event.domain.model.EventId
import com.example.mykku.event.dto.EventPreviewResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.LocalDateTime

data class EventSummary(
    val id: Long,
    val title: String,
    val startedAt: LocalDateTime,
    val expiredAt: LocalDateTime
)

interface EventQueryPort {
    fun findSummaryById(id: EventId): EventSummary?
    fun existsById(id: EventId): Boolean

    // Cross-domain methods (for internal use)
    fun getEventById(eventId: Long): Event
    fun getProcessingEventPreviews(): List<EventPreviewResponse>
    fun getEventsWithPagination(
        status: EventStatusType,
        sortType: EventSortType,
        pageable: Pageable,
        currentTime: LocalDateTime
    ): Page<Event>
    fun getEventByIdWithRelations(eventId: Long): Event
    fun getEventImages(events: List<Event>): Map<Long, List<EventImage>>
}
