package com.example.mykku.feed.tool

import com.example.mykku.feed.domain.Event
import com.example.mykku.feed.domain.EventImage
import com.example.mykku.feed.domain.EventSortType
import com.example.mykku.feed.domain.EventTag
import com.example.mykku.feed.dto.EventPreviewResponse
import com.example.mykku.feed.exception.FeedException
import com.example.mykku.feed.repository.EventImageRepository
import com.example.mykku.feed.repository.EventRepository
import com.example.mykku.feed.repository.EventTagRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class EventReader(
    private val eventRepository: EventRepository,
    private val eventImageRepository: EventImageRepository,
    private val eventTagRepository: EventTagRepository,
) {
    fun getEventById(eventId: Long): Event {
        return eventRepository.findByIdOrNull(eventId)
            ?: throw FeedException.feedNotFound()
    }

    fun getProcessingEventPreviews(): List<EventPreviewResponse> {
        val events = eventRepository.getByEventPreviews(LocalDateTime.now()).take(5)
        val eventImages = eventImageRepository.findByEventIn(events)
        val imagesByEvent = eventImages.groupBy { it.event }

        return events.map { event ->
            EventPreviewResponse(event, imagesByEvent[event] ?: emptyList())
        }
    }

    fun getEventsWithPagination(
        status: String,
        sortType: EventSortType,
        pageable: Pageable,
        currentTime: LocalDateTime
    ): Page<Event> {
        return when (status) {
            "active" -> getActiveEventsBySortType(sortType, currentTime, pageable)
            "expired" -> eventRepository.findExpiredEventsWithPagination(currentTime, pageable)
            "all" -> eventRepository.findAllEventsWithPagination(pageable)
            else -> throw FeedException.invalidEventStatus()
        }
    }

    private fun getActiveEventsBySortType(
        sortType: EventSortType,
        currentTime: LocalDateTime,
        pageable: Pageable
    ): Page<Event> {
        return when (sortType) {
            EventSortType.LATEST -> eventRepository.findActiveEventsByLatest(currentTime, pageable)
            EventSortType.OLDEST -> eventRepository.findActiveEventsByOldest(currentTime, pageable)
            EventSortType.POPULAR -> eventRepository.findActiveEventsByPopular(currentTime, pageable)
        }
    }

    fun getEventByIdWithRelations(eventId: Long): Event {
        return eventRepository.findByIdOrNull(eventId)
            ?: throw FeedException.eventNotFound()
    }

    fun getEventImages(events: List<Event>): Map<Long, List<EventImage>> {
        val images = eventImageRepository.findByEventIn(events)
        return images.groupBy { it.event.id!! }
    }

    fun getEventTags(events: List<Event>): Map<Long, List<EventTag>> {
        val tags = eventTagRepository.findByEventIn(events)
        return tags.groupBy { it.event.id!! }
    }
}
