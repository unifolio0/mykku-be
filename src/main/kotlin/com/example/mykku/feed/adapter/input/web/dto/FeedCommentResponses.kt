package com.example.mykku.feed.adapter.input.web.dto

import com.example.mykku.feed.application.dto.FeedCommentsResult
import com.example.mykku.feed.application.dto.SingleFeedCommentResult
import java.time.LocalDateTime

data class FeedCommentResponse(
    val id: Long,
    val content: String,
    val author: CommentAuthorResponse?,
    val likeCount: Int,
    val isLiked: Boolean,
    val replies: List<FeedCommentReplyResponse>,
    val replyCount: Int,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

data class CommentAuthorResponse(
    val memberId: String?,
    val nickname: String?,
    val profileImage: String?
)

data class FeedCommentReplyResponse(
    val id: Long,
    val content: String,
    val author: CommentAuthorResponse?,
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
) {
    companion object {
        fun from(result: FeedCommentsResult): FeedCommentsResponse {
            return FeedCommentsResponse(
                comments = result.comments.map { commentResult ->
                    FeedCommentResponse(
                        id = commentResult.id,
                        content = commentResult.content,
                        author = commentResult.author?.let {
                            CommentAuthorResponse(
                                memberId = it.memberId,
                                nickname = it.nickname,
                                profileImage = it.profileImage
                            )
                        },
                        likeCount = commentResult.likeCount,
                        isLiked = commentResult.isLiked,
                        replies = commentResult.replies.map { replyResult ->
                            FeedCommentReplyResponse(
                                id = replyResult.id,
                                content = replyResult.content,
                                author = replyResult.author?.let {
                                    CommentAuthorResponse(
                                        memberId = it.memberId,
                                        nickname = it.nickname,
                                        profileImage = it.profileImage
                                    )
                                },
                                likeCount = replyResult.likeCount,
                                isLiked = replyResult.isLiked,
                                createdAt = replyResult.createdAt,
                                updatedAt = replyResult.updatedAt
                            )
                        },
                        replyCount = commentResult.replyCount,
                        createdAt = commentResult.createdAt,
                        updatedAt = commentResult.updatedAt
                    )
                },
                totalElements = result.totalElements,
                totalPages = result.totalPages,
                currentPage = result.currentPage,
                pageSize = result.pageSize,
                hasNext = result.hasNext
            )
        }
    }
}

data class SingleFeedCommentResponse(
    val id: Long,
    val content: String,
    val author: CommentAuthorResponse?,
    val likeCount: Int,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(result: SingleFeedCommentResult): SingleFeedCommentResponse {
            return SingleFeedCommentResponse(
                id = result.id,
                content = result.content,
                author = result.author?.let {
                    CommentAuthorResponse(
                        memberId = it.memberId,
                        nickname = it.nickname,
                        profileImage = it.profileImage
                    )
                },
                likeCount = result.likeCount,
                createdAt = result.createdAt
            )
        }
    }
}
