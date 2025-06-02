package com.example.mykku.member.tool

import com.example.mykku.feed.domain.Feed
import com.example.mykku.member.repository.LikeFeedRepository
import org.springframework.stereotype.Component

@Component
class LikeFeedReader(
    private val likeFeedRepository: LikeFeedRepository,
) {
    fun isLiked(memberId: String, feed: Feed): Boolean {
        return likeFeedRepository.existsByMemberIdAndFeed(memberId, feed)
    }
}
