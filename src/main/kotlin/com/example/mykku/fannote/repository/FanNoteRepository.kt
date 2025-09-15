package com.example.mykku.fannote.repository

import com.example.mykku.fannote.domain.FanNote
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FanNoteRepository : JpaRepository<FanNote, Long>
