package com.example.mykku.dailymessage.dto

import java.time.LocalDateTime

data class DailyMessageResponse(
    val id: Long,
    val title: String,
    val content: String,
    val createdAt: LocalDateTime
)

data class CommentResponse(
    val id: Long,
    val content: String,
    val likeCount: Int,
    val memberName: String,
    val profileImage: String,
    val createdAt: LocalDateTime,
    val replies: List<ReplyResponse>
)

data class ReplyResponse(
    val id: Long,
    val content: String,
    val likeCount: Int,
    val memberName: String,
    val profileImage: String,
    val createdAt: LocalDateTime
)

data class DailyMessageCommentsResponse(
    val comments: List<CommentResponse>,
    val totalElements: Long,
    val totalPages: Int,
    val currentPage: Int,
    val pageSize: Int,
    val hasNext: Boolean
)
