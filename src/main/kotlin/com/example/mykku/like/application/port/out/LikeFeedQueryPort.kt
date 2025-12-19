package com.example.mykku.like.application.port.out

import com.example.mykku.feed.domain.Feed

interface LikeFeedQueryPort {
    fun isLiked(memberId: String, feed: Feed): Boolean
    fun validateLikeFeedNotExists(memberId: String, feedId: Long)
    fun validateLikeFeedExists(memberId: String, feedId: Long)
    fun getLikedFeedsByMember(memberId: String, feeds: List<Feed>): Set<Long>
}
