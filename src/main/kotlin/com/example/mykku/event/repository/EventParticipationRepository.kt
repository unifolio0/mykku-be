package com.example.mykku.event.repository

import com.example.mykku.event.domain.Event
import com.example.mykku.event.domain.EventParticipation
import com.example.mykku.member.domain.Member
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EventParticipationRepository : JpaRepository<EventParticipation, Long> {
    fun existsByMemberAndEvent(member: Member, event: Event): Boolean
    fun findByEvent(event: Event, pageable: Pageable): Page<EventParticipation>
    fun countByEvent(event: Event): Long
    fun findByMemberAndEventIn(member: Member, events: List<Event>): List<EventParticipation>
}
