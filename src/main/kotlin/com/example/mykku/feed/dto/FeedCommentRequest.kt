package com.example.mykku.feed.dto

data class CreateFeedCommentRequest(
    val content: String,
    val parentCommentId: Long? = null
)

data class UpdateFeedCommentRequest(
    val content: String
)
