package com.example.mykku.event.application.port.out

import com.example.mykku.event.domain.Event
import com.example.mykku.event.domain.EventParticipation
import com.example.mykku.member.domain.Member
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface EventParticipationQueryPort {
    fun existsByMemberAndEvent(member: Member, event: Event): Boolean
    fun getParticipationsByEvent(event: Event, pageable: Pageable): Page<EventParticipation>
    fun countByEvent(event: Event): Long
    fun getParticipatedEventIds(member: Member, events: List<Event>): Set<Long>
    fun getParticipatedEvents(member: Member, pageable: Pageable): Page<Event>
}
