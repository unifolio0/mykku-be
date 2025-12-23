package com.example.mykku.like.domain.model

import com.example.mykku.board.domain.model.BoardId
import com.example.mykku.member.domain.model.MemberId
import java.time.Instant

class LikeBoardDomain private constructor(
    val id: LikeBoardId?,
    val memberId: MemberId,
    val boardId: BoardId,
    val createdAt: Instant
) {
    companion object {
        fun create(
            memberId: MemberId,
            boardId: BoardId
        ): LikeBoardDomain {
            return LikeBoardDomain(
                id = null,
                memberId = memberId,
                boardId = boardId,
                createdAt = Instant.now()
            )
        }

        fun reconstitute(
            id: LikeBoardId,
            memberId: MemberId,
            boardId: BoardId,
            createdAt: Instant
        ): LikeBoardDomain {
            return LikeBoardDomain(
                id = id,
                memberId = memberId,
                boardId = boardId,
                createdAt = createdAt
            )
        }
    }
}
