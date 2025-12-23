package com.example.mykku.like.infrastructure.adapter

import com.example.mykku.feed.domain.Feed
import com.example.mykku.like.application.port.out.LikeFeedQueryPort
import com.example.mykku.like.exception.LikeException
import com.example.mykku.like.repository.LikeFeedRepository
import org.springframework.stereotype.Component

@Component
class LikeFeedQueryAdapter(
    private val likeFeedRepository: LikeFeedRepository
) : LikeFeedQueryPort {

    override fun isLiked(memberId: String, feed: Feed): Boolean {
        return likeFeedRepository.existsByMemberIdAndFeed(memberId, feed)
    }

    override fun validateLikeFeedNotExists(memberId: String, feedId: Long) {
        if (likeFeedRepository.existsByMemberIdAndFeedId(memberId, feedId)) {
            throw LikeException.likeFeedAlreadyLiked()
        }
    }

    override fun validateLikeFeedExists(memberId: String, feedId: Long) {
        if (!likeFeedRepository.existsByMemberIdAndFeedId(memberId, feedId)) {
            throw LikeException.likeFeedNotFound()
        }
    }

    override fun getLikedFeedsByMember(memberId: String, feeds: List<Feed>): Set<Long> {
        val feedIds = feeds.mapNotNull { it.id }
        return likeFeedRepository.findByMemberIdAndFeedIdIn(memberId, feedIds)
            .map { it.feed.id!! }
            .toSet()
    }
}
