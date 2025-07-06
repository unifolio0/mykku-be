package com.example.mykku.like.dto

import com.example.mykku.like.domain.LikeBoard

data class LikeBoardResponse(
    val id: Long,
    val memberId: String,
    val boardId: Long,
) {
    constructor(likeBoard: LikeBoard) : this(
        id = likeBoard.id!!,
        memberId = likeBoard.member.id,
        boardId = likeBoard.board.id!!
    )
}
