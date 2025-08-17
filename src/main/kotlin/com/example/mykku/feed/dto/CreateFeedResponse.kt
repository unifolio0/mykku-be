package com.example.mykku.feed.dto

import java.time.LocalDateTime

data class CreateFeedResponse(
    val id: Long,
    val title: String,
    val content: String,
    val boardId: Long,
    val boardTitle: String,
    val authorId: String,
    val authorNickname: String,
    val authorProfileUrl: String?,
    val images: List<FeedImageResponse>,
    val tags: List<String>,
    val likeCount: Int,
    val commentCount: Int,
    val createdAt: LocalDateTime
)