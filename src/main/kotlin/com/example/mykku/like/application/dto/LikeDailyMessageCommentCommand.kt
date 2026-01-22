package com.example.mykku.like.application.dto

data class LikeDailyMessageCommentCommand(
    val memberId: String,
    val dailyMessageCommentId: Long
)

data class UnlikeDailyMessageCommentCommand(
    val memberId: String,
    val dailyMessageCommentId: Long
)
