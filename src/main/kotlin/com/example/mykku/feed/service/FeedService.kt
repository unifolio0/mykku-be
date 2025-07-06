package com.example.mykku.feed.service

import com.example.mykku.feed.domain.Feed
import com.example.mykku.feed.dto.AuthorResponse
import com.example.mykku.feed.dto.FeedResponse
import com.example.mykku.feed.dto.FeedsResponse
import com.example.mykku.feed.tool.FeedReader
import com.example.mykku.like.tool.LikeFeedReader
import com.example.mykku.member.tool.MemberReader
import com.example.mykku.member.tool.SaveFeedReader
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FeedService(
    private val feedReader: FeedReader,
    private val memberReader: MemberReader,
    private val likeFeedReader: LikeFeedReader,
    private val saveFeedReader: SaveFeedReader
) {
    @Transactional(readOnly = true)
    fun getFeeds(memberId: String): FeedsResponse {
        val follower = memberReader.getFollowerByMemberId(memberId)
        val feeds = feedReader.getFeedsByFollower(follower)
        return FeedsResponse(
            feeds = feeds.map { feed -> getFeedResponse(memberId, feed) }
        )
    }

    private fun getFeedResponse(memberId: String, feed: Feed): FeedResponse {
        val authorResponse = AuthorResponse(feed.member)
        val isLiked = likeFeedReader.isLiked(memberId, feed)
        val isSaved = saveFeedReader.isSaved(memberId, feed)
        return FeedResponse(
            feed = feed,
            author = authorResponse,
            isLiked = isLiked,
            isSaved = isSaved,
        )
    }
}
