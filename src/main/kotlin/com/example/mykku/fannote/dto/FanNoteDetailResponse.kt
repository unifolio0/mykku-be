package com.example.mykku.fannote.dto

import com.example.mykku.fannote.domain.FanNote
import com.example.mykku.fannote.domain.FanNotePage

data class FanNoteDetailResponse(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val content: String?,
    val productionDate: String,
    val coverImageUrl: String?,
    val pages: List<FanNotePageResponse>
) {
    companion object {
        fun from(fanNote: FanNote, pages: List<FanNotePage>): FanNoteDetailResponse {
            return FanNoteDetailResponse(
                id = fanNote.id,
                title = fanNote.title,
                subtitle = fanNote.subtitle,
                content = fanNote.content,
                productionDate = fanNote.productionDate.toString(),
                coverImageUrl = fanNote.coverImageUrl,
                pages = pages
                    .sortedBy { it.pageNumber }
                    .map { FanNotePageResponse.from(it) }
            )
        }
    }
}
