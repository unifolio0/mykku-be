package com.example.mykku.fannote.tool

import com.example.mykku.fannote.domain.FanNote
import com.example.mykku.fannote.domain.FanNotePage
import com.example.mykku.fannote.repository.FanNotePageRepository
import com.example.mykku.fannote.repository.FanNoteRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class FanNoteWriter(
    private val fanNoteRepository: FanNoteRepository,
    private val fanNotePageRepository: FanNotePageRepository
) {

    @Transactional
    fun save(fanNote: FanNote): FanNote {
        return fanNoteRepository.save(fanNote)
    }

    @Transactional
    fun savePage(page: FanNotePage): FanNotePage {
        return fanNotePageRepository.save(page)
    }

    @Transactional
    fun saveAllPages(pages: List<FanNotePage>) {
        fanNotePageRepository.saveAll(pages)
    }

    @Transactional
    fun deleteById(id: Long) {
        val pages = fanNotePageRepository.findByFanNoteIdOrderByPageNumber(id)
        fanNotePageRepository.deleteAll(pages)
        fanNoteRepository.deleteById(id)
    }
}
