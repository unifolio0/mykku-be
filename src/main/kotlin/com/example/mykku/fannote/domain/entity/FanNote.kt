package com.example.mykku.fannote.domain.entity

import com.example.mykku.fannote.domain.vo.FanNoteId
import java.time.LocalDate
import java.time.LocalDateTime

class FanNote private constructor(
    val id: FanNoteId,
    val title: String,
    val subtitle: String?,
    val content: String?,
    val productionDate: LocalDate,
    val coverImageUrl: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun create(
            title: String,
            subtitle: String?,
            content: String?,
            productionDate: LocalDate,
            coverImageUrl: String?
        ): FanNote {
            val now = LocalDateTime.now()
            return FanNote(
                id = FanNoteId(0L),
                title = title,
                subtitle = subtitle,
                content = content,
                productionDate = productionDate,
                coverImageUrl = coverImageUrl,
                createdAt = now,
                updatedAt = now
            )
        }

        fun reconstitute(
            id: FanNoteId,
            title: String,
            subtitle: String?,
            content: String?,
            productionDate: LocalDate,
            coverImageUrl: String?,
            createdAt: LocalDateTime,
            updatedAt: LocalDateTime
        ): FanNote {
            return FanNote(
                id = id,
                title = title,
                subtitle = subtitle,
                content = content,
                productionDate = productionDate,
                coverImageUrl = coverImageUrl,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }
    }

    fun updateInfo(
        title: String,
        subtitle: String?,
        content: String?,
        productionDate: LocalDate,
        coverImageUrl: String?
    ): FanNote {
        return FanNote(
            id = this.id,
            title = title,
            subtitle = subtitle,
            content = content,
            productionDate = productionDate,
            coverImageUrl = coverImageUrl,
            createdAt = this.createdAt,
            updatedAt = LocalDateTime.now()
        )
    }
}
