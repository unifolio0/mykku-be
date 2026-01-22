package com.example.mykku.feed.application.port.output

import com.example.mykku.feed.adapter.output.persistence.entity.FeedJpaEntity
import com.example.mykku.feed.adapter.output.persistence.entity.FeedTagJpaEntity
import com.example.mykku.feed.domain.vo.FeedId

interface FeedTagRepository {
    fun saveAll(feedTagJpaEntities: List<FeedTagJpaEntity>): List<FeedTagJpaEntity>
    fun findByFeed(feed: FeedJpaEntity): List<FeedTagJpaEntity>
    fun findByFeedIn(feeds: List<FeedJpaEntity>): List<FeedTagJpaEntity>
    fun deleteAllByFeed(feed: FeedJpaEntity)
}
