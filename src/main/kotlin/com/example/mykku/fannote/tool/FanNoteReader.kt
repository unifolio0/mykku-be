package com.example.mykku.fannote.tool

import com.example.mykku.fannote.exception.FanNoteException
import com.example.mykku.fannote.domain.FanNote
import com.example.mykku.fannote.domain.FanNotePage
import com.example.mykku.fannote.repository.FanNoteRepository
import com.example.mykku.fannote.repository.FanNotePageRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class FanNoteReader(
    private val fanNoteRepository: FanNoteRepository,
    private val fanNotePageRepository: FanNotePageRepository
) {

    fun findAllWithPagination(pageable: Pageable): Page<FanNote> {
        return fanNoteRepository.findAll(pageable)
    }

    fun findById(id: Long): FanNote {
        return fanNoteRepository.findById(id).orElseThrow {
            FanNoteException.fanNoteNotFound()
        }
    }

    fun findPagesByFanNoteId(fanNoteId: Long): List<FanNotePage> {
        return fanNotePageRepository.findByFanNoteIdOrderByPageNumber(fanNoteId)
    }

    fun existsById(id: Long): Boolean {
        return fanNoteRepository.existsById(id)
    }
}
