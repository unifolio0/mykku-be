package com.example.mykku.feed.adapter.output.persistence

import com.example.mykku.feed.adapter.output.persistence.entity.FeedImageJpaEntity
import com.example.mykku.feed.adapter.output.persistence.entity.FeedJpaEntity
import com.example.mykku.feed.application.port.output.FeedImageRepository
import org.springframework.stereotype.Repository

@Repository
class FeedImageRepositoryAdapter(
    private val feedImageJpaRepository: FeedImageJpaRepository
) : FeedImageRepository {

    override fun saveAll(feedImageJpaEntities: List<FeedImageJpaEntity>): List<FeedImageJpaEntity> {
        return feedImageJpaRepository.saveAll(feedImageJpaEntities)
    }

    override fun findByFeed(feed: FeedJpaEntity): List<FeedImageJpaEntity> {
        return feedImageJpaRepository.findByFeed(feed)
    }

    override fun findByFeedIn(feeds: List<FeedJpaEntity>): List<FeedImageJpaEntity> {
        if (feeds.isEmpty()) return emptyList()
        return feedImageJpaRepository.findByFeedIn(feeds)
    }

    override fun findAllByIdInAndFeed(ids: List<Long>, feed: FeedJpaEntity): List<FeedImageJpaEntity> {
        if (ids.isEmpty()) return emptyList()
        return feedImageJpaRepository.findAllByIdInAndFeed(ids, feed)
    }

    override fun deleteAll(feedImageJpaEntities: List<FeedImageJpaEntity>) {
        if (feedImageJpaEntities.isNotEmpty()) {
            feedImageJpaRepository.deleteAll(feedImageJpaEntities)
        }
    }

    override fun deleteAllByFeed(feed: FeedJpaEntity) {
        feedImageJpaRepository.deleteAllByFeed(feed)
    }
}
