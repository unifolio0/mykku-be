package com.example.mykku.feed.adapter.output.persistence

import com.example.mykku.feed.adapter.output.persistence.entity.FeedJpaEntity
import com.example.mykku.feed.adapter.output.persistence.entity.FeedTagJpaEntity
import com.example.mykku.feed.application.port.output.FeedTagRepository
import org.springframework.stereotype.Repository

@Repository
class FeedTagRepositoryAdapter(
    private val feedTagJpaRepository: FeedTagJpaRepository
) : FeedTagRepository {

    override fun saveAll(feedTagJpaEntities: List<FeedTagJpaEntity>): List<FeedTagJpaEntity> {
        return feedTagJpaRepository.saveAll(feedTagJpaEntities)
    }

    override fun findByFeed(feed: FeedJpaEntity): List<FeedTagJpaEntity> {
        return feedTagJpaRepository.findByFeed(feed)
    }

    override fun findByFeedIn(feeds: List<FeedJpaEntity>): List<FeedTagJpaEntity> {
        if (feeds.isEmpty()) return emptyList()
        return feedTagJpaRepository.findByFeedIn(feeds)
    }

    override fun deleteAllByFeed(feed: FeedJpaEntity) {
        feedTagJpaRepository.deleteAllByFeed(feed)
    }
}
