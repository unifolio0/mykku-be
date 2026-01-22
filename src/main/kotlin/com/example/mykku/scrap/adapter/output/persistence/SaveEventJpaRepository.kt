package com.example.mykku.scrap.adapter.output.persistence

import com.example.mykku.scrap.adapter.output.persistence.entity.SaveEventJpaEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SaveEventJpaRepository : JpaRepository<SaveEventJpaEntity, Long> {
    fun existsByMemberIdAndEventId(memberId: String, eventId: Long): Boolean
    fun findByMemberId(memberId: String, pageable: Pageable): Page<SaveEventJpaEntity>
    fun deleteByMemberIdAndEventId(memberId: String, eventId: Long)
    fun findByMemberIdAndEventIdIn(memberId: String, eventIds: List<Long>): List<SaveEventJpaEntity>
}
