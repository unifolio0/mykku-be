package com.example.mykku.event.application.usecase

import com.example.mykku.event.application.dto.GetMyAwardEventsQuery
import com.example.mykku.event.application.dto.MyAwardEventResult
import com.example.mykku.event.application.dto.PagedMyAwardEventsResult
import com.example.mykku.event.application.port.input.GetMyAwardEventsUseCase
import com.example.mykku.event.application.port.output.EventRepository
import com.example.mykku.event.application.port.output.EventWinnerRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetMyAwardEventsUseCaseImpl(
    private val eventWinnerRepository: EventWinnerRepository,
    private val eventRepository: EventRepository
) : GetMyAwardEventsUseCase {

    @Transactional(readOnly = true)
    override fun execute(query: GetMyAwardEventsQuery): PagedMyAwardEventsResult {
        val winnerPage = eventWinnerRepository.findByMemberId(query.memberId, query.pageable)

        val eventIds = winnerPage.content.map { it.eventId }.distinct()
        val eventsMap = eventRepository.findAllByIds(eventIds).associateBy { it.id.value }

        val results = winnerPage.content.mapNotNull { winner ->
            val event = eventsMap[winner.eventId.value] ?: return@mapNotNull null
            MyAwardEventResult(
                eventId = event.id.value,
                eventTitle = event.title,
                thumbnailUrl = event.thumbnailUrl,
                startedAt = event.startedAt,
                expiredAt = event.expiredAt
            )
        }

        return PagedMyAwardEventsResult(
            content = results,
            page = winnerPage.number,
            size = winnerPage.size,
            totalElements = winnerPage.totalElements,
            totalPages = winnerPage.totalPages,
            isLast = winnerPage.isLast
        )
    }
}
