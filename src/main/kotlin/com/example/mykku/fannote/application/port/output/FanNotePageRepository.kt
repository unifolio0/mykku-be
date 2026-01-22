package com.example.mykku.fannote.application.port.output

import com.example.mykku.fannote.domain.entity.FanNotePage
import com.example.mykku.fannote.domain.vo.FanNoteId
import com.example.mykku.fannote.domain.vo.FanNotePageId

interface FanNotePageRepository {
    fun save(page: FanNotePage): FanNotePage
    fun saveAll(pages: List<FanNotePage>): List<FanNotePage>
    fun findById(id: FanNotePageId): FanNotePage?
    fun findByFanNoteIdOrderByPageNumber(fanNoteId: FanNoteId): List<FanNotePage>
    fun deleteByFanNoteId(fanNoteId: FanNoteId)
    fun deleteAll(pages: List<FanNotePage>)
}
