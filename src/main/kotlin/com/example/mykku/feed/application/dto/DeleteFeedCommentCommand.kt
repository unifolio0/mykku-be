package com.example.mykku.feed.application.dto

data class DeleteFeedCommentCommand(
    val commentId: Long,
    val memberId: String
)
