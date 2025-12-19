package com.example.mykku.fannote.infrastructure.adapter

import com.example.mykku.fannote.application.port.out.FanNoteRepositoryPort
import com.example.mykku.fannote.domain.FanNote
import com.example.mykku.fannote.domain.FanNotePage
import com.example.mykku.fannote.repository.FanNotePageRepository
import com.example.mykku.fannote.repository.FanNoteRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class FanNoteRepositoryAdapter(
    private val fanNoteRepository: FanNoteRepository,
    private val fanNotePageRepository: FanNotePageRepository
) : FanNoteRepositoryPort {

    @Transactional
    override fun save(fanNote: FanNote): FanNote {
        return fanNoteRepository.save(fanNote)
    }

    @Transactional
    override fun savePage(page: FanNotePage): FanNotePage {
        return fanNotePageRepository.save(page)
    }

    @Transactional
    override fun saveAllPages(pages: List<FanNotePage>) {
        fanNotePageRepository.saveAll(pages)
    }

    @Transactional
    override fun deleteById(id: Long) {
        val pages = fanNotePageRepository.findByFanNoteIdOrderByPageNumber(id)
        fanNotePageRepository.deleteAll(pages)
        fanNoteRepository.deleteById(id)
    }
}
