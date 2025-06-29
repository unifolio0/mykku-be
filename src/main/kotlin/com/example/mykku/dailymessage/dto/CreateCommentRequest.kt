package com.example.mykku.dailymessage.dto

data class CreateCommentRequest(
    val content: String,
    val parentCommentId: Long? = null
)
