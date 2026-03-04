package com.example.mykku.like.domain.entity

import com.example.mykku.like.domain.vo.LikeBoardId
import java.time.LocalDateTime

class LikeBoardEntity private constructor(
    val id: LikeBoardId?,
    val memberId: Long,
    val boardId: Long,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun create(
            memberId: Long,
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
            memberId: Long,
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
