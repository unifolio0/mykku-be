package com.example.mykku.like.application.port.out

import com.example.mykku.like.domain.LikeBoard

interface LikeBoardQueryPort {
    fun validateLikeBoardNotExists(memberId: String, boardId: Long)
    fun validateLikeBoardExists(memberId: String, boardId: Long)
    fun getLikedBoards(memberId: String): List<LikeBoard>
}
