package com.example.mykku.feed.adapter.input.web.dto

import com.example.mykku.feed.application.dto.FeedDetailResult
import java.time.LocalDateTime

data class FeedDetailResponse(
    val id: Long,
    val author: AuthorResponse,
    val boardId: Long,
    val boardTitle: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val title: String,
    val content: String,
    val images: List<FeedImageResponse>,
    val tags: List<TagResponse>,
    val likeCount: Int,
    val isLiked: Boolean,
    val isSaved: Boolean,
    val commentCount: Int
) {
    companion object {
        fun from(result: FeedDetailResult): FeedDetailResponse = FeedDetailResponse(
            id = result.id,
            author = AuthorResponse(
                memberId = result.author.memberId,
                nickname = result.author.nickname,
                profileImage = result.author.profileImage,
                role = result.author.role
            ),
            boardId = result.boardId,
            boardTitle = result.boardTitle,
            createdAt = result.createdAt,
            updatedAt = result.updatedAt,
            title = result.title,
            content = result.content,
            images = result.images.map { FeedImageResponse(it.id, it.url, it.width, it.height) },
            tags = result.tags.map { TagResponse(it.title, it.isContest) },
            likeCount = result.likeCount,
            isLiked = result.isLiked,
            isSaved = result.isSaved,
            commentCount = result.commentCount
        )
    }
}
