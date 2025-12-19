package com.example.mykku.event.infrastructure.adapter

import com.example.mykku.event.application.port.out.EventParticipationQueryPort
import com.example.mykku.event.domain.Event
import com.example.mykku.event.domain.EventParticipation
import com.example.mykku.event.repository.EventParticipationRepository
import com.example.mykku.member.domain.Member
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class EventParticipationQueryAdapter(
    private val eventParticipationRepository: EventParticipationRepository
) : EventParticipationQueryPort {

    override fun existsByMemberAndEvent(member: Member, event: Event): Boolean {
        return eventParticipationRepository.existsByMemberAndEvent(member, event)
    }

    override fun getParticipationsByEvent(event: Event, pageable: Pageable): Page<EventParticipation> {
        return eventParticipationRepository.findByEvent(event, pageable)
    }

    override fun countByEvent(event: Event): Long {
        return eventParticipationRepository.countByEvent(event)
    }

    override fun getParticipatedEventIds(member: Member, events: List<Event>): Set<Long> {
        return eventParticipationRepository.findByMemberAndEventIn(member, events)
            .mapNotNull { it.event.id }
            .toSet()
    }

    override fun getParticipatedEvents(member: Member, pageable: Pageable): Page<Event> {
        return eventParticipationRepository.findEventsByMember(member, pageable)
    }
}
