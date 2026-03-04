package com.example.mykku.scrap.adapter.output.persistence

import com.example.mykku.scrap.adapter.output.persistence.entity.SaveFanNoteJpaEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SaveFanNoteJpaRepository : JpaRepository<SaveFanNoteJpaEntity, Long> {
    fun existsByMemberIdAndFanNoteId(memberId: Long, fanNoteId: Long): Boolean
    fun findByMemberId(memberId: Long, pageable: Pageable): Page<SaveFanNoteJpaEntity>
    fun deleteByMemberIdAndFanNoteId(memberId: Long, fanNoteId: Long)
}
