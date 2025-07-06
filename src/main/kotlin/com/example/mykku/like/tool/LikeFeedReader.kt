package com.example.mykku.like.tool

import com.example.mykku.exception.ErrorCode
import com.example.mykku.exception.MykkuException
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
            throw MykkuException(ErrorCode.LIKE_FEED_ALREADY_LIKED)
        }
    }

    fun validateLikeFeedExists(memberId: String, feedId: Long) {
        if (!likeFeedRepository.existsByMemberIdAndFeedId(memberId, feedId)) {
            throw MykkuException(ErrorCode.LIKE_FEED_NOT_FOUND)
        }
    }
}
