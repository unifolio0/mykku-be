package com.example.mykku.event.infrastructure.adapter

import com.example.mykku.event.application.port.out.EventQueryPort
import com.example.mykku.event.application.port.out.EventSummary
import com.example.mykku.event.domain.Event
import com.example.mykku.event.domain.EventImage
import com.example.mykku.event.domain.EventSortType
import com.example.mykku.event.domain.EventStatusType
import com.example.mykku.event.domain.model.EventId
import com.example.mykku.event.dto.EventPreviewResponse
import com.example.mykku.event.exception.EventException
import com.example.mykku.event.repository.EventImageRepository
import com.example.mykku.event.repository.EventRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class EventQueryAdapter(
    private val eventRepository: EventRepository,
    private val eventImageRepository: EventImageRepository
) : EventQueryPort {

    override fun findSummaryById(id: EventId): EventSummary? {
        return eventRepository.findById(id.value)
            .map { event ->
                EventSummary(
                    id = event.id!!,
                    title = event.title,
                    startedAt = event.startedAt,
                    expiredAt = event.expiredAt
                )
            }
            .orElse(null)
    }

    override fun existsById(id: EventId): Boolean {
        return eventRepository.existsById(id.value)
    }

    override fun getEventById(eventId: Long): Event {
        return eventRepository.findByIdOrNull(eventId)
            ?: throw EventException.eventNotFound()
    }

    override fun getProcessingEventPreviews(): List<EventPreviewResponse> {
        val events = eventRepository.findByExpiredAtAfter(LocalDateTime.now()).take(5)
        val eventImages = eventImageRepository.findByEventIn(events)
        val imagesByEvent = eventImages.groupBy { it.event }

        return events.map { event ->
            EventPreviewResponse(event, imagesByEvent[event] ?: emptyList())
        }
    }

    override fun getEventsWithPagination(
        status: EventStatusType,
        sortType: EventSortType,
        pageable: Pageable,
        currentTime: LocalDateTime
    ): Page<Event> {
        return when (status) {
            EventStatusType.ACTIVE -> getActiveEventsBySortType(sortType, currentTime, pageable)
            EventStatusType.EXPIRED -> eventRepository.findByExpiredAtLessThanEqualOrderByCreatedAtDesc(currentTime, pageable)
            EventStatusType.WINNER_SELECTING,
            EventStatusType.WINNER_SELECTED -> eventRepository.findByExpiredAtLessThanEqualOrderByCreatedAtDesc(currentTime, pageable)
            EventStatusType.ALL -> eventRepository.findAllByOrderByCreatedAtDesc(pageable)
        }
    }

    private fun getActiveEventsBySortType(
        sortType: EventSortType,
        currentTime: LocalDateTime,
        pageable: Pageable
    ): Page<Event> {
        return when (sortType) {
            EventSortType.LATEST -> eventRepository.findByExpiredAtAfterOrderByCreatedAtDesc(currentTime, pageable)
            EventSortType.OLDEST -> eventRepository.findByExpiredAtAfterOrderByCreatedAtAsc(currentTime, pageable)
            EventSortType.POPULAR -> eventRepository.findActiveEventsByPopular(currentTime, pageable)
        }
    }

    override fun getEventByIdWithRelations(eventId: Long): Event {
        return eventRepository.findByIdOrNull(eventId)
            ?: throw EventException.eventNotFound()
    }

    override fun getEventImages(events: List<Event>): Map<Long, List<EventImage>> {
        val images = eventImageRepository.findByEventIn(events)
        return images.groupBy { it.event.id!! }
    }
}
