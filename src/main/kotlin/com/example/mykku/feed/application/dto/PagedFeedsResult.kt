package com.example.mykku.feed.application.dto

import java.time.LocalDateTime

data class PagedFeedsResult(
    val feeds: List<FeedResult>,
    val currentPage: Int,
    val totalPages: Int,
    val totalElements: Long,
    val size: Int,
    val hasNext: Boolean,
    val hasPrevious: Boolean
)

data class FeedResult(
    val id: Long,
    val author: AuthorResult?,
    val board: String,
    val createdAt: LocalDateTime,
    val title: String,
    val content: String,
    val images: List<FeedImageResult>,
    val tags: List<TagResult>,
    val likeCount: Int,
    val isLiked: Boolean,
    val commentCount: Int,
    val comment: CommentPreviewResult
)

data class CommentPreviewResult(
    val profileImage: String?,
    val content: String
)
