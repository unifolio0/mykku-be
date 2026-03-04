package com.example.mykku.like.application.dto

data class LikeBoardCommand(
    val memberId: Long,
    val boardId: Long
)

data class UnlikeBoardCommand(
    val memberId: Long,
    val boardId: Long
)

data class GetLikedBoardsQuery(
    val memberId: Long,
    val page: Int,
    val size: Int
)
