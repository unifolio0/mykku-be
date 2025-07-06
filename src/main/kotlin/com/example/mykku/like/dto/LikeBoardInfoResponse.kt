package com.example.mykku.like.dto

import com.example.mykku.like.domain.LikeBoard

data class LikeBoardInfoResponse(
    val id: Long,
    val title: String,
    val logo: String,
) {
    constructor(likeBoard: LikeBoard) : this(
        id = likeBoard.id!!,
        title = likeBoard.board.title,
        logo = likeBoard.board.logo
    )
}
