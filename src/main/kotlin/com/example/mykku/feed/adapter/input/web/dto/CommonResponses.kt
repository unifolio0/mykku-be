package com.example.mykku.feed.adapter.input.web.dto

import com.example.mykku.role.adapter.input.web.RoleResponse

data class AuthorResponse(
    val memberId: String?,
    val nickname: String?,
    val profileImage: String,
    val role: RoleResponse?
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
