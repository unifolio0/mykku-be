package com.example.mykku.feed.application.dto

import com.example.mykku.role.application.dto.RoleResult
import java.time.LocalDateTime

data class FeedDetailResult(
    val id: Long,
    val author: AuthorResult?,
    val boardId: Long,
    val boardTitle: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val title: String,
    val content: String,
    val images: List<FeedImageResult>,
    val tags: List<TagResult>,
    val likeCount: Int,
    val isLiked: Boolean,
    val commentCount: Int
)

data class AuthorResult(
    val memberId: String?,
    val nickname: String?,
    val profileImage: String,
    val role: RoleResult?
)

data class TagResult(
    val title: String,
    val isContest: Boolean
)
