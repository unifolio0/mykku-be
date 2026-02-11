package com.example.mykku.feed.adapter.input.web.dto

data class AuthorResponse(
    val memberId: String?,
    val nickname: String?,
    val profileImage: String,
    val role: String
)

data class TagResponse(
    val title: String,
    val isContest: Boolean
)

data class FeedImageResponse(
    val id: Long,
    val url: String,
    val width: Int,
    val height: Int
)
