package com.example.mykku.feed.adapter.input.web.dto

import com.example.mykku.feed.application.dto.CreateFeedResult
import java.time.LocalDateTime

data class CreateFeedResponse(
    val id: Long,
    val title: String,
    val content: String,
    val boardId: Long,
    val boardTitle: String,
    val authorId: String?,
    val authorNickname: String?,
    val authorProfileUrl: String?,
    val images: List<FeedImageResponse>,
    val tags: List<String>,
    val likeCount: Int,
    val commentCount: Int,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(result: CreateFeedResult): CreateFeedResponse = CreateFeedResponse(
            id = result.id,
            title = result.title,
            content = result.content,
            boardId = result.boardId,
            boardTitle = result.boardTitle,
            authorId = result.authorId,
            authorNickname = result.authorNickname,
            authorProfileUrl = result.authorProfileUrl,
            images = result.images.map { FeedImageResponse(it.id, it.url, it.width, it.height) },
            tags = result.tags,
            likeCount = result.likeCount,
            commentCount = result.commentCount,
            createdAt = result.createdAt
        )
    }
}
