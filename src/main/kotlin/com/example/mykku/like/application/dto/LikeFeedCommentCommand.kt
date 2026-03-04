package com.example.mykku.like.application.dto

data class LikeFeedCommentCommand(
    val memberId: Long,
    val feedCommentId: Long
)

data class UnlikeFeedCommentCommand(
    val memberId: Long,
    val feedCommentId: Long
)
