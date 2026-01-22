package com.example.mykku.feed.application.dto

data class GetFeedDetailQuery(
    val feedId: Long,
    val memberId: String?
)
