package com.example.mykku.event.tool

import com.example.mykku.event.domain.Event
import com.example.mykku.event.domain.EventParticipation
import com.example.mykku.event.repository.EventParticipationRepository
import com.example.mykku.member.domain.Member
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class EventParticipationReader(
    private val eventParticipationRepository: EventParticipationRepository
) {
    fun existsByMemberAndEvent(member: Member, event: Event): Boolean {
        return eventParticipationRepository.existsByMemberAndEvent(member, event)
    }

    fun getParticipationsByEvent(event: Event, pageable: Pageable): Page<EventParticipation> {
        return eventParticipationRepository.findByEvent(event, pageable)
    }

    fun countByEvent(event: Event): Long {
        return eventParticipationRepository.countByEvent(event)
    }

    fun getParticipatedEventIds(member: Member, events: List<Event>): Set<Long> {
        return eventParticipationRepository.findByMemberAndEventIn(member, events)
            .mapNotNull { it.event.id }
            .toSet()
    }

    fun getParticipatedEvents(member: Member, pageable: Pageable): Page<Event> {
        return eventParticipationRepository.findEventsByMember(member, pageable)
    }
}
