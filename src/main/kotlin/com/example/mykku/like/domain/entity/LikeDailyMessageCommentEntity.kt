package com.example.mykku.like.domain.entity

import com.example.mykku.like.domain.vo.LikeDailyMessageCommentId
import java.time.LocalDateTime

class LikeDailyMessageCommentEntity private constructor(
    val id: LikeDailyMessageCommentId?,
    val memberId: String,
    val dailyMessageCommentId: Long,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun create(
            memberId: String,
            dailyMessageCommentId: Long
        ): LikeDailyMessageCommentEntity {
            val now = LocalDateTime.now()
            return LikeDailyMessageCommentEntity(
                id = null,
                memberId = memberId,
                dailyMessageCommentId = dailyMessageCommentId,
                createdAt = now,
                updatedAt = now
            )
        }

        fun reconstitute(
            id: Long,
            memberId: String,
            dailyMessageCommentId: Long,
            createdAt: LocalDateTime,
            updatedAt: LocalDateTime
        ): LikeDailyMessageCommentEntity {
            return LikeDailyMessageCommentEntity(
                id = LikeDailyMessageCommentId.of(id),
                memberId = memberId,
                dailyMessageCommentId = dailyMessageCommentId,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }
    }
}
