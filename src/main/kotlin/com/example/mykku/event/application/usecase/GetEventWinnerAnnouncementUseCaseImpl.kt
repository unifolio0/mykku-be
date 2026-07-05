package com.example.mykku.event.application.usecase

import com.example.mykku.event.application.dto.EventWinnerAnnouncementResult
import com.example.mykku.event.application.port.input.GetEventWinnerAnnouncementUseCase
import com.example.mykku.event.application.port.output.EventRepository
import com.example.mykku.event.application.port.output.EventWinnerAnnouncementRepository
import com.example.mykku.event.domain.vo.EventId
import com.example.mykku.event.exception.EventException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetEventWinnerAnnouncementUseCaseImpl(
    private val eventRepository: EventRepository,
    private val eventWinnerAnnouncementRepository: EventWinnerAnnouncementRepository
) : GetEventWinnerAnnouncementUseCase {

    @Transactional(readOnly = true)
    override fun execute(eventId: Long): EventWinnerAnnouncementResult {
        val event = eventRepository.findById(EventId.of(eventId))
            ?: throw EventException.eventNotFound()

        val announcement = eventWinnerAnnouncementRepository.findByEventId(event.id)
            ?: throw EventException.winnerAnnouncementNotFound()

        return EventWinnerAnnouncementResult(
            eventId = event.id.value,
            eventTitle = event.title,
            title = announcement.title,
            content = announcement.content,
            announcedAt = announcement.announcedAt
        )
    }
}
