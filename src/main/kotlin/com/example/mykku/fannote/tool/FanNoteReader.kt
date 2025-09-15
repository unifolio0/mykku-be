package com.example.mykku.fannote.tool

import com.example.mykku.exception.ErrorCode
import com.example.mykku.exception.MykkuException
import com.example.mykku.fannote.domain.FanNote
import com.example.mykku.fannote.repository.FanNoteRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class FanNoteReader(
    private val fanNoteRepository: FanNoteRepository
) {

    fun findAllWithPagination(pageable: Pageable): Page<FanNote> {
        return fanNoteRepository.findAllOrderByProductionDateDesc(pageable)
    }

    fun findByIdWithPages(id: Long): FanNote {
        return fanNoteRepository.findByIdWithPages(id)
            ?: throw MykkuException(ErrorCode.FAN_NOTE_NOT_FOUND)
    }

    fun existsById(id: Long): Boolean {
        return fanNoteRepository.existsById(id)
    }
}
