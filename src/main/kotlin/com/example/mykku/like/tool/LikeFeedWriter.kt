package com.example.mykku.like.tool

import com.example.mykku.feed.domain.Feed
import com.example.mykku.like.domain.LikeFeed
import com.example.mykku.like.repository.LikeFeedRepository
import com.example.mykku.member.domain.Member
import org.springframework.stereotype.Component

@Component
class LikeFeedWriter(
    private val likeFeedRepository: LikeFeedRepository
) {
    fun createLikeFeed(feed: Feed, member: Member): LikeFeed {
        val likeFeed = LikeFeed(
            member = member,
            feed = feed
        )
        return likeFeedRepository.save(likeFeed)
    }

    fun deleteLikeFeed(memberId: String, feedId: Long) {
        likeFeedRepository.deleteByMemberIdAndFeedId(memberId, feedId)
    }
}
