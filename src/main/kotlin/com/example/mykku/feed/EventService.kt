package com.example.mykku.feed

import com.example.mykku.common.util.PageableValidator
import com.example.mykku.feed.domain.Event
import com.example.mykku.feed.domain.EventSortType
import com.example.mykku.feed.domain.EventStatusType
import com.example.mykku.feed.dto.*
import com.example.mykku.feed.tool.EventDtoConverter
import com.example.mykku.feed.tool.EventReader
import com.example.mykku.feed.tool.EventWriter
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
        val (event, eventImages, eventTags) = eventWriter.createEvent(
            title = request.title,
            isContest = request.isContest,
            expiredAt = request.expiredAt,
            imageRequests = request.images,
            tagTitles = request.tags
        )

        return CreateEventResponse(
            id = event.id!!,
            title = event.title,
            isContest = event.isContest,
            expiredAt = event.expiredAt,
            images = eventImages
                .sortedBy { it.orderIndex }
                .map { EventImageResponse(url = it.url, orderIndex = it.orderIndex) },
            tags = eventTags.map { it.title },
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
        val tagsByEventId = eventReader.getEventTags(events)
        val savedEventIds = saveEventReader.getSavedEventIds(member, events)

        return events.map { event ->
            val images = imagesByEventId[event.id!!] ?: emptyList()
            val tags = tagsByEventId[event.id!!] ?: emptyList()
            val isSaved = savedEventIds.contains(event.id)
            eventDtoConverter.toEventListResponse(event, images, tags, isSaved)
        }
    }

    @Transactional(readOnly = true)
    fun getEventDetail(eventId: Long, member: Member): EventDetailResponse {
        val event = eventReader.getEventByIdWithRelations(eventId)
        val images = eventReader.getEventImages(listOf(event))[event.id] ?: emptyList()
        val tags = eventReader.getEventTags(listOf(event))[event.id] ?: emptyList()
        val isSaved = saveEventReader.isSaved(member, event)

        return eventDtoConverter.toEventDetailResponse(event, images, tags, isSaved)
    }
}
