package com.example.mykku.feed.adapter.output.persistence

import com.example.mykku.feed.adapter.output.persistence.entity.FeedJpaEntity
import com.example.mykku.feed.adapter.output.persistence.entity.FeedTagJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FeedTagJpaRepository : JpaRepository<FeedTagJpaEntity, Long> {
    fun findByFeed(feed: FeedJpaEntity): List<FeedTagJpaEntity>
    fun findByFeedIn(feeds: List<FeedJpaEntity>): List<FeedTagJpaEntity>

    fun deleteAllByFeed(feed: FeedJpaEntity)
}
