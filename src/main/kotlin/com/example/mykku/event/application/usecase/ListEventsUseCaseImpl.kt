package com.example.mykku.event.application.usecase

import com.example.mykku.common.util.PageableValidator
import com.example.mykku.event.application.dto.EventListQuery
import com.example.mykku.event.application.dto.EventListResult
import com.example.mykku.event.application.dto.PagedEventsResult
import com.example.mykku.event.application.port.input.ListEventsUseCase
import com.example.mykku.event.application.port.output.EventImageRepository
import com.example.mykku.event.application.port.output.EventRepository
import com.example.mykku.event.domain.entity.Event
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class ListEventsUseCaseImpl(
    private val eventRepository: EventRepository,
    private val eventImageRepository: EventImageRepository
) : ListEventsUseCase {

    @Transactional(readOnly = true)
    override fun execute(query: EventListQuery): PagedEventsResult {
        val pageable = PageableValidator.validateAndCreate(query.page, query.size)

        val eventPage = eventRepository.findWithPagination(
            query.status,
            query.sortType,
            pageable,
            LocalDateTime.now()
        )

        val eventIds = eventPage.content.map { it.id }
        val imagesByEventId = eventImageRepository.findByEventIds(eventIds)
            .groupBy { it.eventId.value }

        val eventListResults = eventPage.content.map { event ->
            toEventListResult(event, imagesByEventId)
        }

        return PagedEventsResult(
            content = eventListResults,
            page = eventPage.number,
            size = eventPage.size,
            totalElements = eventPage.totalElements,
            totalPages = eventPage.totalPages,
            isLast = eventPage.isLast
        )
    }

    private fun toEventListResult(
        event: Event,
        imagesByEventId: Map<Long, List<com.example.mykku.event.domain.entity.EventImage>>
    ): EventListResult {
        val images = imagesByEventId[event.id.value] ?: emptyList()

        return EventListResult(
            id = event.id.value,
            title = event.title,
            startedAt = event.startedAt,
            expiredAt = event.expiredAt,
            status = event.status,
            thumbnailUrl = images.sortedBy { it.orderIndex }.firstOrNull()?.url,
            isSaved = false
        )
    }
}
