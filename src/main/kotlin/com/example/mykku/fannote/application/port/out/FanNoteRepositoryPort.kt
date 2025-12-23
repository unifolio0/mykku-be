package com.example.mykku.fannote.application.port.out

import com.example.mykku.fannote.domain.FanNote
import com.example.mykku.fannote.domain.FanNotePage

interface FanNoteRepositoryPort {
    fun save(fanNote: FanNote): FanNote
    fun savePage(page: FanNotePage): FanNotePage
    fun saveAllPages(pages: List<FanNotePage>)
    fun deleteById(id: Long)
}
