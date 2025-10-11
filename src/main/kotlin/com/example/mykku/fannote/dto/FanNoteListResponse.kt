package com.example.mykku.fannote.dto

import com.example.mykku.fannote.domain.FanNote

data class FanNoteListResponse(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val content: String?,
    val productionDate: String,
    val coverImageUrl: String?
) {
    companion object {
        fun from(fanNote: FanNote): FanNoteListResponse {
            return FanNoteListResponse(
                id = fanNote.id!!,
                title = fanNote.title,
                subtitle = fanNote.subtitle,
                content = fanNote.content,
                productionDate = fanNote.productionDate.toString(),
                coverImageUrl = fanNote.coverImageUrl
            )
        }
    }
}
