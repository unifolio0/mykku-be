package com.example.mykku.like.application.dto

data class LikeFeedResult(
    val id: Long,
    val memberId: String,
    val feedId: Long
)

data class LikeFeedCommentResult(
    val id: Long,
    val memberId: String,
    val feedCommentId: Long
)

data class LikeBoardResult(
    val id: Long,
    val memberId: String,
    val boardId: Long
)

data class LikeBoardInfoResult(
    val id: Long,
    val title: String,
    val logo: String
)

data class LikeDailyMessageCommentResult(
    val id: Long,
    val memberId: String,
    val dailyMessageCommentId: Long
)
