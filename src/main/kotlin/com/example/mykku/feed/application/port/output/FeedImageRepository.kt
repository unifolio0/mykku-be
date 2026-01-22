package com.example.mykku.feed.application.port.output

import com.example.mykku.feed.adapter.output.persistence.entity.FeedImageJpaEntity
import com.example.mykku.feed.adapter.output.persistence.entity.FeedJpaEntity
import com.example.mykku.feed.domain.vo.FeedId

interface FeedImageRepository {
    fun saveAll(feedImageJpaEntities: List<FeedImageJpaEntity>): List<FeedImageJpaEntity>
    fun findByFeed(feed: FeedJpaEntity): List<FeedImageJpaEntity>
    fun findByFeedIn(feeds: List<FeedJpaEntity>): List<FeedImageJpaEntity>
    fun findAllByIdInAndFeed(ids: List<Long>, feed: FeedJpaEntity): List<FeedImageJpaEntity>
    fun deleteAll(feedImageJpaEntities: List<FeedImageJpaEntity>)
    fun deleteAllByFeed(feed: FeedJpaEntity)
}
