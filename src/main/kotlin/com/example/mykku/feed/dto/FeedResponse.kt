package com.example.mykku.feed.dto

import java.time.LocalDateTime

data class FeedResponse(
    val id: Long,
    val author: AuthorResponse,
    val board: String,
    val createdAt: LocalDateTime,
    val title: String,
    val content: String,
    val images: List<String>,
    val tags: List<String>,
    val likeCount: Int,
    val isLiked: Boolean,
    val isSaved: Boolean,
    val commentCount: Int,
    val comment: CommentPreviewResponse,
)
