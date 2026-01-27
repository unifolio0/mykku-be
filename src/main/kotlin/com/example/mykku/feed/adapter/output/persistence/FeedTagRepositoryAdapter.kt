package com.example.mykku.feed.adapter.output.persistence

import com.example.mykku.feed.adapter.output.persistence.entity.FeedTagJpaEntity
import com.example.mykku.feed.application.port.output.FeedTagRepository
import com.example.mykku.feed.domain.entity.FeedTag
import com.example.mykku.feed.domain.vo.FeedId
import org.springframework.stereotype.Repository

@Repository
class FeedTagRepositoryAdapter(
    private val feedTagJpaRepository: FeedTagJpaRepository,
    private val feedJpaRepository: FeedJpaRepository
) : FeedTagRepository {

    override fun saveAll(feedTags: List<FeedTag>, feedId: FeedId): List<FeedTag> {
        if (feedTags.isEmpty()) return emptyList()
        val feed = feedJpaRepository.findById(feedId.value)
            .orElseThrow { IllegalArgumentException("Feed not found: ${feedId.value}") }
        val jpaEntities = feedTags.map { FeedTagJpaEntity.fromDomain(it, feed) }
        return feedTagJpaRepository.saveAll(jpaEntities).map { it.toDomain() }
    }

    override fun findByFeedId(feedId: FeedId): List<FeedTag> {
        val feed = feedJpaRepository.findById(feedId.value).orElse(null) ?: return emptyList()
        return feedTagJpaRepository.findByFeed(feed).map { it.toDomain() }
    }

    override fun findByFeedIds(feedIds: List<FeedId>): List<FeedTag> {
        if (feedIds.isEmpty()) return emptyList()
        val feeds = feedJpaRepository.findAllById(feedIds.map { it.value })
        if (feeds.isEmpty()) return emptyList()
        return feedTagJpaRepository.findByFeedIn(feeds).map { it.toDomain() }
    }

    override fun deleteAllByFeedId(feedId: FeedId) {
        val feed = feedJpaRepository.findById(feedId.value).orElse(null) ?: return
        feedTagJpaRepository.deleteAllByFeed(feed)
    }
}
