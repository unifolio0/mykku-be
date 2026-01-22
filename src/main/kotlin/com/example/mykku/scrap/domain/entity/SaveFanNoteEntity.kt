package com.example.mykku.scrap.domain.entity

import com.example.mykku.scrap.domain.vo.SaveFanNoteId
import java.time.LocalDateTime

class SaveFanNoteEntity private constructor(
    val id: SaveFanNoteId?,
    val memberId: String,
    val fanNoteId: Long,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun create(
            memberId: String,
            fanNoteId: Long
        ): SaveFanNoteEntity {
            val now = LocalDateTime.now()
            return SaveFanNoteEntity(
                id = null,
                memberId = memberId,
                fanNoteId = fanNoteId,
                createdAt = now,
                updatedAt = now
            )
        }

        fun reconstitute(
            id: Long,
            memberId: String,
            fanNoteId: Long,
            createdAt: LocalDateTime,
            updatedAt: LocalDateTime
        ): SaveFanNoteEntity {
            return SaveFanNoteEntity(
                id = SaveFanNoteId.of(id),
                memberId = memberId,
                fanNoteId = fanNoteId,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }
    }
}
