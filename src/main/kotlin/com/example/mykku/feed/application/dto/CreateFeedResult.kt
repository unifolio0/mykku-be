package com.example.mykku.feed.application.dto

import java.time.LocalDateTime

data class CreateFeedResult(
    val id: Long,
    val title: String,
    val content: String,
    val boardId: Long,
    val boardTitle: String,
    val authorId: String?,
    val authorNickname: String?,
    val authorProfileUrl: String?,
    val images: List<FeedImageResult>,
    val tags: List<String>,
    val likeCount: Int,
    val commentCount: Int,
    val createdAt: LocalDateTime
)

data class FeedImageResult(
    val id: Long,
    val url: String,
    val width: Int,
    val height: Int
)
