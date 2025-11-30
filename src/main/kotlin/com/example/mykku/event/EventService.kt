package com.example.mykku.event

import com.example.mykku.common.util.PageableValidator
import com.example.mykku.event.domain.Event
import com.example.mykku.event.domain.EventSortType
import com.example.mykku.event.domain.EventStatusType
import com.example.mykku.event.dto.*
import com.example.mykku.event.tool.EventDtoConverter
import com.example.mykku.event.tool.EventReader
import com.example.mykku.event.tool.EventWriter
import com.example.mykku.member.domain.Member
import com.example.mykku.scrap.tool.SaveEventReader
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class EventService(
    private val eventWriter: EventWriter,
    private val eventReader: EventReader,
    private val saveEventReader: SaveEventReader,
    private val eventDtoConverter: EventDtoConverter
) {

    @Transactional
    fun createEvent(request: CreateEventRequest): CreateEventResponse {
        val (event, eventImages) = eventWriter.createEvent(
            title = request.title,
            description = request.description,
            expiredAt = request.expiredAt,
            imageRequests = request.images
        )

        return CreateEventResponse(
            id = event.id!!,
            title = event.title,
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
        val eventPage = eventReader.getEventsWithPagination(status, sortType, pageable, LocalDateTime.now())

        val eventListResponses = convertToEventListResponses(eventPage.content, member)
        val responseMap = eventListResponses.associateBy { it.id }
        val responsePage = eventPage.map { event ->
            responseMap[event.id]!!
        }

        return PagedEventsResponse.from(responsePage)
    }

    private fun convertToEventListResponses(events: List<Event>, member: Member): List<EventListResponse> {
        val imagesByEventId = eventReader.getEventImages(events)
        val savedEventIds = saveEventReader.getSavedEventIds(member, events)

        return events.map { event ->
            val images = imagesByEventId[event.id!!] ?: emptyList()
            val isSaved = savedEventIds.contains(event.id)
            eventDtoConverter.toEventListResponse(event, images, isSaved)
        }
    }

    @Transactional(readOnly = true)
    fun getEventDetail(eventId: Long, member: Member): EventDetailResponse {
        val event = eventReader.getEventByIdWithRelations(eventId)
        val images = eventReader.getEventImages(listOf(event))[event.id] ?: emptyList()
        val isSaved = saveEventReader.isSaved(member, event)

        return eventDtoConverter.toEventDetailResponse(event, images, isSaved)
    }
}
