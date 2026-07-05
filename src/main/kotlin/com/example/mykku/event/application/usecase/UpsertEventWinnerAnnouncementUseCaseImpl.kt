package com.example.mykku.event.application.usecase

import com.example.mykku.event.application.dto.EventWinnerAnnouncementResult
import com.example.mykku.event.application.dto.UpsertEventWinnerAnnouncementCommand
import com.example.mykku.event.application.port.input.UpsertEventWinnerAnnouncementUseCase
import com.example.mykku.event.application.port.output.EventRepository
import com.example.mykku.event.application.port.output.EventWinnerAnnouncementRepository
import com.example.mykku.event.domain.entity.EventWinnerAnnouncement
import com.example.mykku.event.domain.vo.EventId
import com.example.mykku.event.exception.EventException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpsertEventWinnerAnnouncementUseCaseImpl(
    private val eventRepository: EventRepository,
    private val eventWinnerAnnouncementRepository: EventWinnerAnnouncementRepository
) : UpsertEventWinnerAnnouncementUseCase {

    @Transactional
    override fun execute(command: UpsertEventWinnerAnnouncementCommand): EventWinnerAnnouncementResult {
        val event = eventRepository.findById(EventId.of(command.eventId))
            ?: throw EventException.eventNotFound()

        val existing = eventWinnerAnnouncementRepository.findByEventId(event.id)
        val announcement = existing?.update(command.title, command.content, command.announcedAt)
            ?: EventWinnerAnnouncement.create(event.id, command.title, command.content, command.announcedAt)

        val saved = eventWinnerAnnouncementRepository.save(announcement)

        return EventWinnerAnnouncementResult(
            eventId = event.id.value,
            eventTitle = event.title,
            title = saved.title,
            content = saved.content,
            announcedAt = saved.announcedAt
        )
    }
}
