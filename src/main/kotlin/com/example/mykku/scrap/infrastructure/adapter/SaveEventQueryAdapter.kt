package com.example.mykku.scrap.infrastructure.adapter

import com.example.mykku.event.domain.Event
import com.example.mykku.member.domain.Member
import com.example.mykku.scrap.application.port.out.SaveEventQueryPort
import com.example.mykku.scrap.domain.SaveEvent
import com.example.mykku.scrap.repository.SaveEventRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class SaveEventQueryAdapter(
    private val saveEventRepository: SaveEventRepository
) : SaveEventQueryPort {

    override fun isSaved(member: Member, event: Event): Boolean {
        return saveEventRepository.existsByMemberAndEvent(member, event)
    }

    override fun getSavedEvents(member: Member, pageable: Pageable): Page<SaveEvent> {
        return saveEventRepository.findByMember(member, pageable)
    }

    override fun getSavedEventIds(member: Member, events: List<Event>): Set<Long> {
        return saveEventRepository.findByMemberAndEventIn(member, events)
            .mapNotNull { it.event.id }
            .toSet()
    }
}
