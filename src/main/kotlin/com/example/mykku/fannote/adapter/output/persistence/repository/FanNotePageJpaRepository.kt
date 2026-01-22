package com.example.mykku.fannote.adapter.output.persistence.repository

import com.example.mykku.fannote.adapter.output.persistence.entity.FanNotePageJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FanNotePageJpaRepository : JpaRepository<FanNotePageJpaEntity, Long> {
    fun findByFanNoteIdOrderByPageNumber(fanNoteId: Long): List<FanNotePageJpaEntity>
    fun deleteByFanNoteId(fanNoteId: Long)
}
