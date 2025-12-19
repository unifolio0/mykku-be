package com.example.mykku.scrap.repository

import com.example.mykku.event.domain.Event
import com.example.mykku.member.domain.Member
import com.example.mykku.scrap.domain.SaveEvent
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SaveEventRepository : JpaRepository<SaveEvent, Long> {
    fun existsByMemberAndEvent(member: Member, event: Event): Boolean
    fun findByMember(member: Member, pageable: Pageable): Page<SaveEvent>
    fun findByMemberAndEvent(member: Member, event: Event): SaveEvent?
    fun deleteByMemberAndEvent(member: Member, event: Event)
    fun findByMemberAndEventIn(member: Member, events: List<Event>): List<SaveEvent>

    fun existsByMemberIdAndEventId(memberId: String, eventId: Long): Boolean
    fun countByEventId(eventId: Long): Int
}
