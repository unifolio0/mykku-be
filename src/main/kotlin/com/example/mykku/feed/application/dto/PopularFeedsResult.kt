package com.example.mykku.feed.application.dto

data class PopularFeedsResult(
    val feeds: List<PopularFeedResult>
)

data class PopularFeedResult(
    val id: Long,
    val rank: Int,
    val title: String,
    val content: String
)
