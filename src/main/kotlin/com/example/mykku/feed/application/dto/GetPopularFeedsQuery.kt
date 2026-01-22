package com.example.mykku.feed.application.dto

data class GetPopularFeedsQuery(
    val boardId: Long,
    val memberId: String?
)
