package com.example.mykku.feed.adapter.output.persistence

import com.example.mykku.feed.adapter.output.persistence.entity.FeedImageJpaEntity
import com.example.mykku.feed.adapter.output.persistence.entity.FeedJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FeedImageJpaRepository : JpaRepository<FeedImageJpaEntity, Long> {
    fun findByFeed(feed: FeedJpaEntity): List<FeedImageJpaEntity>
    fun findByFeedIn(feeds: List<FeedJpaEntity>): List<FeedImageJpaEntity>

    fun deleteAllByFeed(feed: FeedJpaEntity)
    fun deleteAllByIdIn(ids: List<Long>)
    fun findAllByIdInAndFeed(ids: List<Long>, feed: FeedJpaEntity): List<FeedImageJpaEntity>
}
