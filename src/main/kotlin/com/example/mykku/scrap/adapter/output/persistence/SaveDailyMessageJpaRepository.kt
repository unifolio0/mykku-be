package com.example.mykku.scrap.adapter.output.persistence

import com.example.mykku.scrap.adapter.output.persistence.entity.SaveDailyMessageJpaEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SaveDailyMessageJpaRepository : JpaRepository<SaveDailyMessageJpaEntity, Long> {
    fun existsByMemberIdAndDailyMessageId(memberId: Long, dailyMessageId: Long): Boolean
    fun findByMemberId(memberId: Long, pageable: Pageable): Page<SaveDailyMessageJpaEntity>
    fun deleteByMemberIdAndDailyMessageId(memberId: Long, dailyMessageId: Long)
}
