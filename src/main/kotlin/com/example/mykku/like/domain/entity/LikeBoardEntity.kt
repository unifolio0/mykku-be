package com.example.mykku.like.domain.entity

import com.example.mykku.like.domain.vo.LikeBoardId
import java.time.LocalDateTime

class LikeBoardEntity private constructor(
    val id: LikeBoardId?,
    val memberId: String,
    val boardId: Long,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun create(
            memberId: String,
            boardId: Long
        ): LikeBoardEntity {
            val now = LocalDateTime.now()
            return LikeBoardEntity(
                id = null,
                memberId = memberId,
                boardId = boardId,
                createdAt = now,
                updatedAt = now
            )
        }

        fun reconstitute(
            id: Long,
            memberId: String,
            boardId: Long,
            createdAt: LocalDateTime,
            updatedAt: LocalDateTime
        ): LikeBoardEntity {
            return LikeBoardEntity(
                id = LikeBoardId.of(id),
                memberId = memberId,
                boardId = boardId,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }
    }
}
