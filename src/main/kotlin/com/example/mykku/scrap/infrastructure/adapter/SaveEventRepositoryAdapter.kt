package com.example.mykku.scrap.infrastructure.adapter

import com.example.mykku.event.domain.Event
import com.example.mykku.member.domain.Member
import com.example.mykku.scrap.application.port.out.SaveEventQueryPort
import com.example.mykku.scrap.application.port.out.SaveEventRepositoryPort
import com.example.mykku.scrap.domain.SaveEvent
import com.example.mykku.scrap.exception.ScrapException
import com.example.mykku.scrap.repository.SaveEventRepository
import org.springframework.stereotype.Component

@Component
class SaveEventRepositoryAdapter(
    private val saveEventRepository: SaveEventRepository,
    private val saveEventQueryPort: SaveEventQueryPort
) : SaveEventRepositoryPort {

    override fun saveEvent(member: Member, event: Event): SaveEvent {
        if (saveEventQueryPort.isSaved(member, event)) {
            throw ScrapException.saveEventAlreadyExists()
        }

        val saveEvent = SaveEvent(
            member = member,
            event = event
        )

        return saveEventRepository.save(saveEvent)
    }

    override fun unsaveEvent(member: Member, event: Event) {
        if (!saveEventQueryPort.isSaved(member, event)) {
            throw ScrapException.saveEventNotFound()
        }

        saveEventRepository.deleteByMemberAndEvent(member, event)
    }
}
