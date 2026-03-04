package com.example.mykku.scrap.application.usecase

import com.example.mykku.scrap.application.dto.GetSavedEventsQuery
import com.example.mykku.scrap.application.dto.SaveEventCommand
import com.example.mykku.scrap.application.dto.SaveEventResult
import com.example.mykku.scrap.application.dto.UnsaveEventCommand
import com.example.mykku.scrap.application.port.input.SaveEventUseCase
import com.example.mykku.scrap.application.port.output.SaveEventPort
import com.example.mykku.scrap.domain.entity.SaveEventEntity
import com.example.mykku.scrap.exception.ScrapException
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SaveEventApplicationService(
    private val saveEventPort: SaveEventPort
) : SaveEventUseCase {

    @Transactional
    override fun saveEvent(command: SaveEventCommand) {
        if (saveEventPort.existsByMemberIdAndEventId(command.memberId, command.eventId)) {
            throw ScrapException.saveEventAlreadyExists()
        }

        val saveEvent = SaveEventEntity.create(
            memberId = command.memberId,
            eventId = command.eventId
        )

        saveEventPort.save(saveEvent)
    }

    @Transactional
    override fun unsaveEvent(command: UnsaveEventCommand) {
        if (!saveEventPort.existsByMemberIdAndEventId(command.memberId, command.eventId)) {
            throw ScrapException.saveEventNotFound()
        }
        saveEventPort.deleteByMemberIdAndEventId(command.memberId, command.eventId)
    }

    @Transactional(readOnly = true)
    override fun getSavedEvents(query: GetSavedEventsQuery): Page<SaveEventResult> {
        val pageable = PageRequest.of(
            query.page,
            query.size,
            Sort.by(Sort.Direction.DESC, "createdAt")
        )
        return saveEventPort.findByMemberId(query.memberId, pageable)
    }

    @Transactional(readOnly = true)
    override fun isSaved(memberId: Long, eventId: Long): Boolean {
        return saveEventPort.existsByMemberIdAndEventId(memberId, eventId)
    }

    @Transactional(readOnly = true)
    override fun getSavedEventIds(memberId: Long, eventIds: List<Long>): Set<Long> {
        return saveEventPort.findByMemberIdAndEventIdIn(memberId, eventIds)
            .map { it.eventId }
            .toSet()
    }
}
