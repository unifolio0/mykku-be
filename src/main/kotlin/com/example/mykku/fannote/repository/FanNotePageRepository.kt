package com.example.mykku.fannote.repository

import com.example.mykku.fannote.domain.FanNotePage
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FanNotePageRepository : JpaRepository<FanNotePage, Long> {
    fun findByFanNoteIdOrderByPageNumber(fanNoteId: Long): List<FanNotePage>
}