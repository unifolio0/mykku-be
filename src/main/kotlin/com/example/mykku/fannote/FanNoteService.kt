package com.example.mykku.fannote

import com.example.mykku.fannote.dto.FanNoteDetailResponse
import com.example.mykku.fannote.dto.FanNoteListResponse
import com.example.mykku.fannote.tool.FanNoteReader
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class FanNoteService(
    private val fanNoteReader: FanNoteReader
) {

    fun getFanNoteList(pageable: Pageable): Page<FanNoteListResponse> {
        val fanNotes = fanNoteReader.findAllWithPagination(pageable)
        return fanNotes.map { FanNoteListResponse.from(it) }
    }

    fun getFanNoteDetail(fanNoteId: Long): FanNoteDetailResponse {
        val fanNote = fanNoteReader.findById(fanNoteId)
        val pages = fanNoteReader.findPagesByFanNoteId(fanNoteId)
        return FanNoteDetailResponse.from(fanNote, pages)
    }
}
