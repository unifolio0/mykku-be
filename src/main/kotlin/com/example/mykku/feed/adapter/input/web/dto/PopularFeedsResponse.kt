package com.example.mykku.feed.adapter.input.web.dto

import com.example.mykku.feed.application.dto.PopularFeedsResult

data class PopularFeedResponse(
    val id: Long,
    val rank: Int,
    val title: String,
    val content: String
)

data class PopularFeedsResponse(
    val feeds: List<PopularFeedResponse>
) {
    companion object {
        fun from(result: PopularFeedsResult): PopularFeedsResponse {
            return PopularFeedsResponse(
                feeds = result.feeds.map { feedResult ->
                    PopularFeedResponse(
                        id = feedResult.id,
                        rank = feedResult.rank,
                        title = feedResult.title,
                        content = feedResult.content
                    )
                }
            )
        }
    }
}
