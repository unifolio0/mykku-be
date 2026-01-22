package com.example.mykku.like.domain.entity

import com.example.mykku.like.domain.vo.LikeFeedId
import java.time.LocalDateTime

class LikeFeedEntity private constructor(
    val id: LikeFeedId?,
    val memberId: String,
    val feedId: Long,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun create(
            memberId: String,
            feedId: Long
        ): LikeFeedEntity {
            val now = LocalDateTime.now()
            return LikeFeedEntity(
                id = null,
                memberId = memberId,
                feedId = feedId,
                createdAt = now,
                updatedAt = now
            )
        }

        fun reconstitute(
            id: Long,
            memberId: String,
            feedId: Long,
            createdAt: LocalDateTime,
            updatedAt: LocalDateTime
        ): LikeFeedEntity {
            return LikeFeedEntity(
                id = LikeFeedId.of(id),
                memberId = memberId,
                feedId = feedId,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }
    }
}
