package com.example.mykku.event.application.usecase

import com.example.mykku.event.application.dto.GetMyEventWinnerStatusQuery
import com.example.mykku.event.application.dto.MyEventWinnerStatusResult
import com.example.mykku.event.application.port.input.GetMyEventWinnerStatusUseCase
import com.example.mykku.event.application.port.output.EventRepository
import com.example.mykku.event.application.port.output.EventWinnerRepository
import com.example.mykku.event.domain.vo.EventId
import com.example.mykku.event.domain.vo.EventStatusType
import com.example.mykku.event.exception.EventException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetMyEventWinnerStatusUseCaseImpl(
    private val eventRepository: EventRepository,
    private val eventWinnerRepository: EventWinnerRepository
) : GetMyEventWinnerStatusUseCase {

    @Transactional(readOnly = true)
    override fun execute(query: GetMyEventWinnerStatusQuery): MyEventWinnerStatusResult {
        val eventId = EventId.of(query.eventId)
        val event = eventRepository.findById(eventId)
            ?: throw EventException.eventNotFound()

        if (event.status != EventStatusType.WINNER_SELECTED) {
            throw EventException.winnerNotAnnounced()
        }

        val myWinner = eventWinnerRepository.findByEventIdAndMemberId(eventId, query.id)
            ?: return MyEventWinnerStatusResult(isWinner = false, winnerId = null)

        return MyEventWinnerStatusResult(isWinner = true, winnerId = myWinner.id.value)
    }
}
