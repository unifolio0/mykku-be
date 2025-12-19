package com.example.mykku.fannote.domain.model

import java.time.Instant
import java.time.LocalDate

class FanNoteDomain private constructor(
    val id: FanNoteId?,
    val title: String,
    val subtitle: String?,
    val content: String?,
    val productionDate: LocalDate,
    val coverImageUrl: String?,
    val createdAt: Instant,
    private var _updatedAt: Instant
) {
    val updatedAt: Instant get() = _updatedAt

    companion object {
        fun create(
            title: String,
            subtitle: String?,
            content: String?,
            productionDate: LocalDate,
            coverImageUrl: String?
        ): FanNoteDomain {
            val now = Instant.now()
            return FanNoteDomain(
                id = null,
                title = title,
                subtitle = subtitle,
                content = content,
                productionDate = productionDate,
                coverImageUrl = coverImageUrl,
                createdAt = now,
                _updatedAt = now
            )
        }

        fun reconstitute(
            id: FanNoteId,
            title: String,
            subtitle: String?,
            content: String?,
            productionDate: LocalDate,
            coverImageUrl: String?,
            createdAt: Instant,
            updatedAt: Instant
        ): FanNoteDomain {
            return FanNoteDomain(
                id = id,
                title = title,
                subtitle = subtitle,
                content = content,
                productionDate = productionDate,
                coverImageUrl = coverImageUrl,
                createdAt = createdAt,
                _updatedAt = updatedAt
            )
        }
    }

    fun updateInfo(
        title: String,
        subtitle: String?,
        content: String?,
        productionDate: LocalDate,
        coverImageUrl: String?
    ): FanNoteDomain {
        return FanNoteDomain(
            id = this.id,
            title = title,
            subtitle = subtitle,
            content = content,
            productionDate = productionDate,
            coverImageUrl = coverImageUrl,
            createdAt = this.createdAt,
            _updatedAt = Instant.now()
        )
    }
}
