package com.example.mykku.event.application.usecase

import com.example.mykku.common.util.PageableValidator
import com.example.mykku.event.application.dto.EventListResult
import com.example.mykku.event.application.dto.PagedEventsResult
import com.example.mykku.event.application.port.input.GetMyParticipatedEventsUseCase
import com.example.mykku.event.application.port.output.EventImageRepository
import com.example.mykku.event.application.port.output.EventParticipationRepository
import com.example.mykku.event.domain.entity.Event
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetMyParticipatedEventsUseCaseImpl(
    private val eventParticipationRepository: EventParticipationRepository,
    private val eventImageRepository: EventImageRepository
) : GetMyParticipatedEventsUseCase {

    @Transactional(readOnly = true)
    override fun execute(memberId: Long, page: Int, size: Int): PagedEventsResult {
        val pageable = PageableValidator.validateAndCreate(page, size)

        val eventPage = eventParticipationRepository.findEventsByMemberId(memberId, pageable)

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
        return EventListResult(
            id = event.id.value,
            title = event.title,
            startedAt = event.startedAt,
            expiredAt = event.expiredAt,
            status = event.status,
            thumbnailUrl = event.thumbnailUrl,
            isSaved = false
        )
    }
}
