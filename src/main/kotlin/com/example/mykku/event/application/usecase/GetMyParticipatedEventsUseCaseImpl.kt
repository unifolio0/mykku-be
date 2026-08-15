package com.example.mykku.event.application.usecase

import com.example.mykku.common.util.PageableValidator
import com.example.mykku.event.application.dto.MyParticipatedEventResult
import com.example.mykku.event.application.dto.PagedMyParticipatedEventsResult
import com.example.mykku.event.application.port.input.GetMyParticipatedEventsUseCase
import com.example.mykku.event.application.port.output.EventParticipationRepository
import com.example.mykku.event.application.port.output.EventWinnerRepository
import com.example.mykku.event.domain.entity.Event
import com.example.mykku.event.domain.vo.EventStatusType
import com.example.mykku.event.domain.vo.EventWinnerStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class GetMyParticipatedEventsUseCaseImpl(
    private val eventParticipationRepository: EventParticipationRepository,
    private val eventWinnerRepository: EventWinnerRepository
) : GetMyParticipatedEventsUseCase {

    @Transactional(readOnly = true)
    override fun execute(memberId: Long, page: Int, size: Int): PagedMyParticipatedEventsResult {
        val pageable = PageableValidator.validateAndCreate(page, size)

        val eventPage = eventParticipationRepository.findEventsByMemberId(memberId, pageable)

        val eventIds = eventPage.content.map { it.id }
        val wonEventIds = eventWinnerRepository.findByMemberIdAndEventIds(memberId, eventIds)
            .map { it.eventId.value }
            .toSet()

        val results = eventPage.content.map { toResult(it, wonEventIds) }

        return PagedMyParticipatedEventsResult(
            content = results,
            page = eventPage.number,
            size = eventPage.size,
            totalElements = eventPage.totalElements,
            totalPages = eventPage.totalPages,
            isLast = eventPage.isLast
        )
    }

    private fun toResult(event: Event, wonEventIds: Set<Long>): MyParticipatedEventResult {
        return MyParticipatedEventResult(
            id = event.id.value,
            title = event.title,
            startedAt = event.startedAt,
            expiredAt = event.expiredAt,
            status = event.resolveStatus(LocalDateTime.now()),
            thumbnailUrl = event.thumbnailUrl,
            winnerStatus = resolveWinnerStatus(event, wonEventIds)
        )
    }

    private fun resolveWinnerStatus(event: Event, wonEventIds: Set<Long>): EventWinnerStatus {
        return when {
            wonEventIds.contains(event.id.value) -> EventWinnerStatus.WON
            event.status != EventStatusType.WINNER_SELECTED -> EventWinnerStatus.PENDING
            else -> EventWinnerStatus.LOST
        }
    }
}
