package com.example.mykku.fannote.adapter.output.persistence.repository

import com.example.mykku.fannote.adapter.output.persistence.entity.FanNoteJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FanNoteJpaRepository : JpaRepository<FanNoteJpaEntity, Long>
