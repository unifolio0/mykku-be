package com.example.mykku.feed.repository

import com.example.mykku.feed.domain.Feed
import com.example.mykku.feed.domain.FeedImage
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FeedImageRepository : JpaRepository<FeedImage, Long> {
    fun findByFeed(feed: Feed): List<FeedImage>
}
