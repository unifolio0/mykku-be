package com.example.mykku.like.application.dto

data class LikeFeedCommentCommand(
    val memberId: String,
    val feedCommentId: Long
)

data class UnlikeFeedCommentCommand(
    val memberId: String,
    val feedCommentId: Long
)
