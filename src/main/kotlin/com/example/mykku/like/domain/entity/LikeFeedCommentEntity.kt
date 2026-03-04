package com.example.mykku.like.domain.entity

import com.example.mykku.like.domain.vo.LikeFeedCommentId
import java.time.LocalDateTime

class LikeFeedCommentEntity private constructor(
    val id: LikeFeedCommentId?,
    val memberId: Long,
    val feedCommentId: Long,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun create(
            memberId: Long,
            feedCommentId: Long
        ): LikeFeedCommentEntity {
            val now = LocalDateTime.now()
            return LikeFeedCommentEntity(
                id = null,
                memberId = memberId,
                feedCommentId = feedCommentId,
                createdAt = now,
                updatedAt = now
            )
        }

        fun reconstitute(
            id: Long,
            memberId: Long,
            feedCommentId: Long,
            createdAt: LocalDateTime,
            updatedAt: LocalDateTime
        ): LikeFeedCommentEntity {
            return LikeFeedCommentEntity(
                id = LikeFeedCommentId.of(id),
                memberId = memberId,
                feedCommentId = feedCommentId,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }
    }
}
