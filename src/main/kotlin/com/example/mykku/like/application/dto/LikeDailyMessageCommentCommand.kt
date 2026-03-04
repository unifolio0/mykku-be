package com.example.mykku.like.application.dto

data class LikeDailyMessageCommentCommand(
    val memberId: Long,
    val dailyMessageCommentId: Long
)

data class UnlikeDailyMessageCommentCommand(
    val memberId: Long,
    val dailyMessageCommentId: Long
)
