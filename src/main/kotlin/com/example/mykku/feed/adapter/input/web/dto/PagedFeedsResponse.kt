package com.example.mykku.feed.adapter.input.web.dto

import com.example.mykku.feed.application.dto.PagedFeedsResult
import java.time.LocalDateTime

data class PagedFeedsResponse(
    val feeds: List<FeedResponse>,
    val currentPage: Int,
    val totalPages: Int,
    val totalElements: Long,
    val size: Int,
    val hasNext: Boolean,
    val hasPrevious: Boolean
) {
    companion object {
        fun from(result: PagedFeedsResult): PagedFeedsResponse = PagedFeedsResponse(
            feeds = result.feeds.map { feedResult ->
                FeedResponse(
                    id = feedResult.id,
                    author = AuthorResponse(
                        memberId = feedResult.author.memberId,
                        nickname = feedResult.author.nickname,
                        profileImage = feedResult.author.profileImage,
                        role = feedResult.author.role
                    ),
                    board = feedResult.board,
                    createdAt = feedResult.createdAt,
                    title = feedResult.title,
                    content = feedResult.content,
                    images = feedResult.images.map { FeedImageResponse(it.id, it.url, it.width, it.height) },
                    tags = feedResult.tags.map { TagResponse(it.title, it.isContest) },
                    likeCount = feedResult.likeCount,
                    isLiked = feedResult.isLiked,
                    isSaved = feedResult.isSaved,
                    commentCount = feedResult.commentCount,
                    comment = CommentPreviewResponse(
                        profileImage = feedResult.comment.profileImage,
                        content = feedResult.comment.content
                    )
                )
            },
            currentPage = result.currentPage,
            totalPages = result.totalPages,
            totalElements = result.totalElements,
            size = result.size,
            hasNext = result.hasNext,
            hasPrevious = result.hasPrevious
        )
    }
}

data class FeedResponse(
    val id: Long,
    val author: AuthorResponse,
    val board: String,
    val createdAt: LocalDateTime,
    val title: String,
    val content: String,
    val images: List<FeedImageResponse>,
    val tags: List<TagResponse>,
    val likeCount: Int,
    val isLiked: Boolean,
    val isSaved: Boolean,
    val commentCount: Int,
    val comment: CommentPreviewResponse
)

data class CommentPreviewResponse(
    val profileImage: String,
    val content: String
)
