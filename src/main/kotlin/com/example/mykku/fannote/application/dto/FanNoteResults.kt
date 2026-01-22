package com.example.mykku.fannote.application.dto

import com.example.mykku.fannote.domain.entity.FanNote
import com.example.mykku.fannote.domain.entity.FanNotePage
import java.time.LocalDate

data class FanNoteListResult(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val content: String?,
    val productionDate: LocalDate,
    val coverImageUrl: String?
) {
    companion object {
        fun from(fanNote: FanNote): FanNoteListResult {
            return FanNoteListResult(
                id = fanNote.id.value,
                title = fanNote.title,
                subtitle = fanNote.subtitle,
                content = fanNote.content,
                productionDate = fanNote.productionDate,
                coverImageUrl = fanNote.coverImageUrl
            )
        }
    }
}

data class FanNoteDetailResult(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val content: String?,
    val productionDate: LocalDate,
    val coverImageUrl: String?,
    val pages: List<FanNotePageResult>
) {
    companion object {
        fun from(fanNote: FanNote, pages: List<FanNotePage>): FanNoteDetailResult {
            return FanNoteDetailResult(
                id = fanNote.id.value,
                title = fanNote.title,
                subtitle = fanNote.subtitle,
                content = fanNote.content,
                productionDate = fanNote.productionDate,
                coverImageUrl = fanNote.coverImageUrl,
                pages = pages
                    .sortedBy { it.pageNumber }
                    .map { FanNotePageResult.from(it) }
            )
        }
    }
}

data class FanNotePageResult(
    val pageNumber: Int,
    val imageUrl: String
) {
    companion object {
        fun from(page: FanNotePage): FanNotePageResult {
            return FanNotePageResult(
                pageNumber = page.pageNumber,
                imageUrl = page.imageUrl
            )
        }
    }
}
