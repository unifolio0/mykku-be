package com.example.mykku.event.adapter.output.persistence.repository

import com.example.mykku.event.adapter.output.persistence.entity.EventJpaEntity
import com.example.mykku.event.adapter.output.persistence.entity.EventParticipationJpaEntity
import com.example.mykku.member.domain.Member
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface EventParticipationJpaRepository : JpaRepository<EventParticipationJpaEntity, Long> {
    fun existsByMemberAndEvent(member: Member, event: EventJpaEntity): Boolean
    fun findByEvent(event: EventJpaEntity, pageable: Pageable): Page<EventParticipationJpaEntity>
    fun countByEvent(event: EventJpaEntity): Long
    fun findByMemberAndEventIn(member: Member, events: List<EventJpaEntity>): List<EventParticipationJpaEntity>

    @Query("SELECT ep.event FROM EventParticipationJpaEntity ep WHERE ep.member = :member ORDER BY ep.createdAt DESC")
    fun findEventsByMember(member: Member, pageable: Pageable): Page<EventJpaEntity>
}
