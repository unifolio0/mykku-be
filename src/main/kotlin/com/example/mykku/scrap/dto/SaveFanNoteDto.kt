package com.example.mykku.scrap.dto

import com.example.mykku.scrap.domain.SaveFanNote
import org.springframework.data.domain.Page

data class SaveFanNoteResponse(
    val id: Long,
    val fanNoteId: Long
) {
    companion object {
        fun from(saveFanNote: SaveFanNote): SaveFanNoteResponse {
            return SaveFanNoteResponse(
                id = saveFanNote.id!!,
                fanNoteId = saveFanNote.fanNote.id!!
            )
        }

        fun fromPage(page: Page<SaveFanNote>): Page<SaveFanNoteResponse> {
            return page.map { from(it) }
        }
    }
}
