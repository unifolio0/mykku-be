package com.example.mykku.feed.service

import com.example.mykku.feed.domain.Feed
import com.example.mykku.feed.dto.FeedResponse
import com.example.mykku.feed.dto.FeedsResponse
import com.example.mykku.feed.tool.FeedReader
import com.example.mykku.member.tool.MemberReader
import org.springframework.stereotype.Service

@Service
class FeedService(
    private val feedReader: FeedReader,
    private val memberReader: MemberReader,
) {
    fun getFeeds(memberId: String): FeedsResponse {
        val follower = memberReader.getFollowerByMemberId(memberId)
        val feeds = feedReader.getFeedsByFollower(follower)
        return FeedsResponse(
            feeds = feeds.map { feed -> getFeedResponse(feed) }
        )
    }

    private fun getFeedResponse(feed: Feed): FeedResponse {
        TODO("Not yet implemented")
    }
}
