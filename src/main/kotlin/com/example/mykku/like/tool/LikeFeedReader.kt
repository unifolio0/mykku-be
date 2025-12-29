package com.example.mykku.like.tool

import com.example.mykku.like.exception.LikeException
import com.example.mykku.feed.domain.Feed
import com.example.mykku.like.repository.LikeFeedRepository
import org.springframework.stereotype.Component

@Component
class LikeFeedReader(
    private val likeFeedRepository: LikeFeedRepository,
) {
    fun isLiked(memberId: String, feed: Feed): Boolean {
        return likeFeedRepository.existsByMemberIdAndFeed(memberId, feed)
    }

    fun validateLikeFeedNotExists(memberId: String, feedId: Long) {
        if (likeFeedRepository.existsByMemberIdAndFeedId(memberId, feedId)) {
            throw LikeException.likeFeedAlreadyLiked()
        }
    }

    fun validateLikeFeedExists(memberId: String, feedId: Long) {
        if (!likeFeedRepository.existsByMemberIdAndFeedId(memberId, feedId)) {
            throw LikeException.likeFeedNotFound()
        }
    }
    
    fun getLikedFeedsByMember(memberId: String, feeds: List<Feed>): Set<Long> {
        val feedIds = feeds.mapNotNull { it.id }
        return likeFeedRepository.findByMemberIdAndFeedIdIn(memberId, feedIds)
            .map { it.feed.id!! }
            .toSet()
    }
}
