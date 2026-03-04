package com.example.mykku.scrap.domain.entity

import com.example.mykku.scrap.domain.vo.SaveDailyMessageId
import java.time.LocalDateTime

class SaveDailyMessageEntity private constructor(
    val id: SaveDailyMessageId?,
    val memberId: Long,
    val dailyMessageId: Long,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun create(
            memberId: Long,
            dailyMessageId: Long
        ): SaveDailyMessageEntity {
            val now = LocalDateTime.now()
            return SaveDailyMessageEntity(
                id = null,
                memberId = memberId,
                dailyMessageId = dailyMessageId,
                createdAt = now,
                updatedAt = now
            )
        }

        fun reconstitute(
            id: Long,
            memberId: Long,
            dailyMessageId: Long,
            createdAt: LocalDateTime,
            updatedAt: LocalDateTime
        ): SaveDailyMessageEntity {
            return SaveDailyMessageEntity(
                id = SaveDailyMessageId.of(id),
                memberId = memberId,
                dailyMessageId = dailyMessageId,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }
    }
}
