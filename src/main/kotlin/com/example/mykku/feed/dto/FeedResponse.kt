package com.example.mykku.feed.dto

import com.example.mykku.feed.domain.Feed
import java.time.LocalDateTime

data class FeedResponse(
    val id: Long,
    val author: AuthorResponse,
    val board: String,
    val createdAt: LocalDateTime,
    val title: String,
    val content: String,
    val images: List<FeedImageResponse>,
    val tags: List<String>,
    val likeCount: Int,
    val isLiked: Boolean,
    val isSaved: Boolean,
    val commentCount: Int,
    val comment: CommentPreviewResponse,
) {
    constructor(feed: Feed, author: AuthorResponse, isLiked: Boolean, isSaved: Boolean) : this(
        id = feed.id!!,
        author = author,
        board = feed.board.title,
        createdAt = feed.createdAt,
        title = feed.title,
        content = feed.content,
        images = feed.feedImages.map { 
            FeedImageResponse(
                url = it.url,
                width = it.width,
                height = it.height
            )
        },
        tags = feed.feedTags.map { it.tag.title },
        likeCount = feed.likeCount,
        isLiked = isLiked,
        isSaved = isSaved,
        commentCount = feed.commentCount,
        comment = getFirstComment(feed)
    )

    companion object {
        private fun getFirstComment(feed: Feed): CommentPreviewResponse {
            val feedComments = feed.feedComments
            if (feedComments.isEmpty()) {
                return CommentPreviewResponse(
                    profileImage = "",
                    content = ""
                )
            }
            return CommentPreviewResponse(feedComments[0])
        }
    }
}
