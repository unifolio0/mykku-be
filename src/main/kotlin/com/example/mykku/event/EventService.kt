package com.example.mykku.event

import com.example.mykku.common.util.PageableValidator
import com.example.mykku.event.application.port.out.EventParticipationQueryPort
import com.example.mykku.event.application.port.out.EventQueryPort
import com.example.mykku.event.application.port.out.EventRepositoryPort
import com.example.mykku.event.domain.Event
import com.example.mykku.event.domain.EventSortType
import com.example.mykku.event.domain.EventStatusType
import com.example.mykku.event.dto.*
import com.example.mykku.event.tool.EventDtoConverter
import com.example.mykku.member.domain.Member
import com.example.mykku.scrap.application.port.out.SaveEventQueryPort
import org.springframework.data.domain.PageImpl
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class EventService(
    private val eventRepositoryPort: EventRepositoryPort,
    private val eventQueryPort: EventQueryPort,
    private val eventParticipationQueryPort: EventParticipationQueryPort,
    private val saveEventQueryPort: SaveEventQueryPort,
    private val eventDtoConverter: EventDtoConverter
) {

    @Transactional
    fun createEvent(request: CreateEventRequest): CreateEventResponse {
        val (event, eventImages) = eventRepositoryPort.createEvent(
            title = request.title,
            description = request.description,
            startedAt = request.startedAt,
            expiredAt = request.expiredAt,
            imageRequests = request.images
        )

        return CreateEventResponse(
            id = event.id!!,
            title = event.title,
            description = event.description,
            startedAt = event.startedAt,
            expiredAt = event.expiredAt,
            images = eventImages
                .sortedBy { it.orderIndex }
                .map { EventImageResponse(url = it.url, orderIndex = it.orderIndex) },
            createdAt = event.createdAt
        )
    }

    @Transactional(readOnly = true)
    fun getEvents(
        status: EventStatusType,
        sortType: EventSortType,
        page: Int,
        size: Int,
        member: Member
    ): PagedEventsResponse {
        val pageable = PageableValidator.validateAndCreate(page, size)
        val eventPage = eventQueryPort.getEventsWithPagination(status, sortType, pageable, LocalDateTime.now())

        val eventListResponses = convertToEventListResponses(eventPage.content, member)
        val responseMap = eventListResponses.associateBy { it.id }
        val responsePage = eventPage.map { event ->
            responseMap[event.id]!!
        }

        return PagedEventsResponse.from(responsePage)
    }

    private fun convertToEventListResponses(events: List<Event>, member: Member): List<EventListResponse> {
        val imagesByEventId = eventQueryPort.getEventImages(events)
        val savedEventIds = saveEventQueryPort.getSavedEventIds(member, events)

        return events.map { event ->
            val images = imagesByEventId[event.id!!] ?: emptyList()
            val isSaved = savedEventIds.contains(event.id)
            eventDtoConverter.toEventListResponse(event, images, isSaved)
        }
    }

    @Transactional(readOnly = true)
    fun getEventDetail(eventId: Long, member: Member): EventDetailResponse {
        val event = eventQueryPort.getEventByIdWithRelations(eventId)
        val images = eventQueryPort.getEventImages(listOf(event))[event.id] ?: emptyList()
        val isSaved = saveEventQueryPort.isSaved(member, event)

        return eventDtoConverter.toEventDetailResponse(event, images, isSaved)
    }

    @Transactional(readOnly = true)
    fun getMyParticipatedEvents(member: Member, page: Int, size: Int): PagedEventsResponse {
        val pageable = PageableValidator.validateAndCreate(page, size)
        val eventPage = eventParticipationQueryPort.getParticipatedEvents(member, pageable)

        val eventListResponses = convertToEventListResponses(eventPage.content, member)
        val responsePage = PageImpl(eventListResponses, eventPage.pageable, eventPage.totalElements)

        return PagedEventsResponse.from(responsePage)
    }
}
