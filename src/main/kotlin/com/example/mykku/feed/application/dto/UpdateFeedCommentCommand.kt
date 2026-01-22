package com.example.mykku.feed.application.dto

data class UpdateFeedCommentCommand(
    val commentId: Long,
    val memberId: String,
    val content: String
)
