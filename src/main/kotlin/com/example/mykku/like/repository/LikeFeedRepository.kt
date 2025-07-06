package com.example.mykku.like.repository

import com.example.mykku.feed.domain.Feed
import com.example.mykku.like.domain.LikeFeed
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LikeFeedRepository : JpaRepository<LikeFeed, Long> {
    fun existsByMemberIdAndFeed(memberId: String, feed: Feed): Boolean

    fun existsByMemberIdAndFeedId(memberId: String, feedId: Long): Boolean

    fun deleteByMemberIdAndFeedId(memberId: String, feedId: Long)
}
