package com.example.mykku.fannote.application.port.output

import com.example.mykku.fannote.domain.entity.FanNote
import com.example.mykku.fannote.domain.vo.FanNoteId
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface FanNoteRepository {
    fun save(fanNote: FanNote): FanNote
    fun findById(id: FanNoteId): FanNote?
    fun findAll(pageable: Pageable): Page<FanNote>
    fun existsById(id: FanNoteId): Boolean
    fun deleteById(id: FanNoteId)
}
