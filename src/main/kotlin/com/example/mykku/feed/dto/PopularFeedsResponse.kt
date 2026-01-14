package com.example.mykku.feed.dto

import com.example.mykku.feed.domain.Feed

data class PopularFeedResponse(
    val id: Long,
    val rank: Int,
    val title: String,
    val content: String
) {
    companion object {
        fun from(feed: Feed, rank: Int): PopularFeedResponse {
            return PopularFeedResponse(
                id = feed.id!!,
                rank = rank,
                title = feed.title,
                content = feed.content
            )
        }
    }
}

data class PopularFeedsResponse(
    val feeds: List<PopularFeedResponse>
) {
    companion object {
        fun from(feeds: List<Feed>): PopularFeedsResponse {
            return PopularFeedsResponse(
                feeds = feeds.mapIndexed { index, feed ->
                    PopularFeedResponse.from(feed, index + 1)
                }
            )
        }
    }
}
