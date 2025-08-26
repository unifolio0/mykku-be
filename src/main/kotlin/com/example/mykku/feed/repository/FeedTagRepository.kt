package com.example.mykku.feed.repository

import com.example.mykku.feed.domain.Feed
import com.example.mykku.feed.domain.FeedTag
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FeedTagRepository : JpaRepository<FeedTag, Long> {
    fun findByFeed(feed: Feed): List<FeedTag>
    fun findByFeedIn(feeds: List<Feed>): List<FeedTag>
}