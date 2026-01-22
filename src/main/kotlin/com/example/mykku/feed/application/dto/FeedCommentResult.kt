package com.example.mykku.feed.application.dto

import java.time.LocalDateTime

data class FeedCommentResult(
    val id: Long,
    val content: String,
    val author: CommentAuthorResult,
    val likeCount: Int,
    val isLiked: Boolean,
    val replies: List<FeedCommentReplyResult>,
    val replyCount: Int,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

data class CommentAuthorResult(
    val memberId: String,
    val nickname: String,
    val profileImage: String
)

data class FeedCommentReplyResult(
    val id: Long,
    val content: String,
    val author: CommentAuthorResult,
    val likeCount: Int,
    val isLiked: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

data class FeedCommentsResult(
    val comments: List<FeedCommentResult>,
    val totalElements: Long,
    val totalPages: Int,
    val currentPage: Int,
    val pageSize: Int,
    val hasNext: Boolean
)

data class SingleFeedCommentResult(
    val id: Long,
    val content: String,
    val author: CommentAuthorResult,
    val likeCount: Int,
    val createdAt: LocalDateTime
)
