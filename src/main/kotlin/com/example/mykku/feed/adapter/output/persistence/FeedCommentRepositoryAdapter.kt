package com.example.mykku.feed.adapter.output.persistence

import com.example.mykku.feed.adapter.output.persistence.entity.FeedCommentJpaEntity
import com.example.mykku.feed.adapter.output.persistence.entity.FeedJpaEntity
import com.example.mykku.feed.application.port.output.FeedCommentRepository
import com.example.mykku.feed.domain.vo.FeedCommentId
import com.example.mykku.feed.exception.FeedException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class FeedCommentRepositoryAdapter(
    private val feedCommentJpaRepository: FeedCommentJpaRepository
) : FeedCommentRepository {

    override fun save(feedCommentJpaEntity: FeedCommentJpaEntity): FeedCommentJpaEntity {
        return feedCommentJpaRepository.save(feedCommentJpaEntity)
    }

    override fun findById(id: FeedCommentId): FeedCommentJpaEntity? {
        return feedCommentJpaRepository.findById(id.value).orElse(null)
    }

    override fun findByIdOrThrow(id: FeedCommentId): FeedCommentJpaEntity {
        return feedCommentJpaRepository.findById(id.value)
            .orElseThrow { FeedException.feedCommentNotFound() }
    }

    override fun findByFeedAndParentCommentIsNull(
        feed: FeedJpaEntity,
        pageable: Pageable
    ): Page<FeedCommentJpaEntity> {
        return feedCommentJpaRepository.findByFeedAndParentCommentIsNull(feed, pageable)
    }

    override fun findByParentComment(parentComment: FeedCommentJpaEntity): List<FeedCommentJpaEntity> {
        return feedCommentJpaRepository.findByParentComment(parentComment)
    }

    override fun findByParentCommentIn(parentComments: List<FeedCommentJpaEntity>): List<FeedCommentJpaEntity> {
        if (parentComments.isEmpty()) return emptyList()
        return feedCommentJpaRepository.findByParentCommentIn(parentComments)
    }

    override fun countByFeed(feed: FeedJpaEntity): Long {
        return feedCommentJpaRepository.countByFeed(feed)
    }

    override fun findIdsByFeed(feed: FeedJpaEntity): List<Long> {
        return feedCommentJpaRepository.findIdsByFeed(feed)
    }

    override fun delete(feedCommentJpaEntity: FeedCommentJpaEntity) {
        feedCommentJpaRepository.delete(feedCommentJpaEntity)
    }

    override fun deleteAllByFeed(feed: FeedJpaEntity) {
        feedCommentJpaRepository.deleteAllByFeed(feed)
    }
}
