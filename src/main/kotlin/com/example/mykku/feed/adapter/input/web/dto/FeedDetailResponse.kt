package com.example.mykku.feed.adapter.input.web.dto

import com.example.mykku.feed.application.dto.FeedDetailResult
import com.example.mykku.role.adapter.input.web.RoleResponse
import java.time.LocalDateTime

data class FeedDetailResponse(
    val id: Long,
    val author: AuthorResponse?,
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
    val commentCount: Int
) {
    companion object {
        fun from(result: FeedDetailResult): FeedDetailResponse = FeedDetailResponse(
            id = result.id,
            author = result.author?.let {
                AuthorResponse(
                    memberId = it.memberId,
                    nickname = it.nickname,
                    profileImage = it.profileImage,
                    role = it.role?.let { role -> RoleResponse(role.id, role.name, role.description) }
                )
            },
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
            commentCount = result.commentCount
        )
    }
}
