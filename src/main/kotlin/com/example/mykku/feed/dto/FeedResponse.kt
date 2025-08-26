package com.example.mykku.feed.dto

import com.example.mykku.feed.domain.Feed
import com.example.mykku.feed.domain.FeedComment
import com.example.mykku.feed.domain.FeedImage
import com.example.mykku.feed.domain.FeedTag
import java.time.LocalDateTime

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
    val comment: CommentPreviewResponse,
) {
    constructor(
        feed: Feed, 
        author: AuthorResponse, 
        isLiked: Boolean, 
        isSaved: Boolean, 
        eventTagTitles: Set<String>,
        feedImages: List<FeedImage> = emptyList(),
        feedTags: List<FeedTag> = emptyList(),
        feedComments: List<FeedComment> = emptyList()
    ) : this(
        id = feed.id!!,
        author = author,
        board = feed.board.title,
        createdAt = feed.createdAt,
        title = feed.title,
        content = feed.content,
        images = feedImages.map {
            FeedImageResponse(
                url = it.url,
                width = it.width,
                height = it.height
            )
        },
        tags = feedTags.map { 
            TagResponse(
                title = it.title,
                isEvent = eventTagTitles.contains(it.title)
            )
        },
        likeCount = feed.likeCount,
        isLiked = isLiked,
        isSaved = isSaved,
        commentCount = feed.commentCount,
        comment = getFirstComment(feedComments)
    )

    companion object {
        private fun getFirstComment(feedComments: List<FeedComment>): CommentPreviewResponse {
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
