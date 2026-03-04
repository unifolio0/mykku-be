package com.example.mykku.scrap.domain.entity

import com.example.mykku.scrap.domain.vo.SaveEventId
import java.time.LocalDateTime

class SaveEventEntity private constructor(
    val id: SaveEventId?,
    val memberId: Long,
    val eventId: Long,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun create(
            memberId: Long,
            eventId: Long
        ): SaveEventEntity {
            val now = LocalDateTime.now()
            return SaveEventEntity(
                id = null,
                memberId = memberId,
                eventId = eventId,
                createdAt = now,
                updatedAt = now
            )
        }

        fun reconstitute(
            id: Long,
            memberId: Long,
            eventId: Long,
            createdAt: LocalDateTime,
            updatedAt: LocalDateTime
        ): SaveEventEntity {
            return SaveEventEntity(
                id = SaveEventId.of(id),
                memberId = memberId,
                eventId = eventId,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }
    }
}
