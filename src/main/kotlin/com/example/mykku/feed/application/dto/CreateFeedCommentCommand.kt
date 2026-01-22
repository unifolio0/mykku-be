package com.example.mykku.feed.application.dto

data class CreateFeedCommentCommand(
    val feedId: Long,
    val memberId: String,
    val content: String,
    val parentCommentId: Long?
)
