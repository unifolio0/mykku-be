package com.example.mykku.event.application.port.output

import com.example.mykku.event.domain.entity.Event
import com.example.mykku.event.domain.entity.EventParticipation
import com.example.mykku.event.domain.vo.EventId
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface EventParticipationRepository {
    fun save(participation: EventParticipation): EventParticipation
    fun findByEventId(eventId: EventId, pageable: Pageable): Page<EventParticipation>
    fun findEventsByMemberId(memberId: String, pageable: Pageable): Page<Event>
    fun findByMemberIdAndEventIds(memberId: String, eventIds: List<EventId>): List<EventParticipation>
    fun existsByMemberIdAndEventId(memberId: String, eventId: EventId): Boolean
    fun countByEventId(eventId: EventId): Long
}
