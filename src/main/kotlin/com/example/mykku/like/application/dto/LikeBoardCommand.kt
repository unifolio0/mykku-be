package com.example.mykku.like.application.dto

data class LikeBoardCommand(
    val memberId: String,
    val boardId: Long
)

data class UnlikeBoardCommand(
    val memberId: String,
    val boardId: Long
)

data class GetLikedBoardsQuery(
    val memberId: String,
    val page: Int,
    val size: Int
)
