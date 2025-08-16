package com.example.mykku.feed.dto

import java.time.LocalDateTime

data class FeedCommentResponse(
    val id: Long,
    val content: String,
    val author: CommentAuthorResponse,
    val likeCount: Int,
    val isLiked: Boolean,
    val replies: List<FeedCommentReplyResponse>,
    val replyCount: Int,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

data class CommentAuthorResponse(
    val memberId: String,
    val nickname: String,
    val profileImage: String
)

data class FeedCommentReplyResponse(
    val id: Long,
    val content: String,
    val author: CommentAuthorResponse,
    val likeCount: Int,
    val isLiked: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

data class FeedCommentsResponse(
    val comments: List<FeedCommentResponse>,
    val totalElements: Long,
    val totalPages: Int,
    val currentPage: Int,
    val pageSize: Int,
    val hasNext: Boolean
)
