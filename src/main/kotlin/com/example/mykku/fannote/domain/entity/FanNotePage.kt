package com.example.mykku.fannote.domain.entity

import com.example.mykku.fannote.domain.vo.FanNoteId
import com.example.mykku.fannote.domain.vo.FanNotePageId
import java.time.LocalDateTime

class FanNotePage private constructor(
    val id: FanNotePageId,
    val fanNoteId: Long,
    val pageNumber: Int,
    val imageUrl: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun create(
            fanNoteId: Long,
            pageNumber: Int,
            imageUrl: String
        ): FanNotePage {
            val now = LocalDateTime.now()
            return FanNotePage(
                id = FanNotePageId(0L),
                fanNoteId = fanNoteId,
                pageNumber = pageNumber,
                imageUrl = imageUrl,
                createdAt = now,
                updatedAt = now
            )
        }

        fun reconstitute(
            id: FanNotePageId,
            fanNoteId: Long,
            pageNumber: Int,
            imageUrl: String,
            createdAt: LocalDateTime,
            updatedAt: LocalDateTime
        ): FanNotePage {
            return FanNotePage(
                id = id,
                fanNoteId = fanNoteId,
                pageNumber = pageNumber,
                imageUrl = imageUrl,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }
    }

    fun updatePage(pageNumber: Int, imageUrl: String): FanNotePage {
        return FanNotePage(
            id = this.id,
            fanNoteId = this.fanNoteId,
            pageNumber = pageNumber,
            imageUrl = imageUrl,
            createdAt = this.createdAt,
            updatedAt = LocalDateTime.now()
        )
    }
}
