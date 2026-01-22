package com.example.mykku.feed.application.port.output

import com.example.mykku.feed.adapter.output.persistence.entity.FeedCommentJpaEntity
import com.example.mykku.feed.adapter.output.persistence.entity.FeedJpaEntity
import com.example.mykku.feed.domain.vo.FeedCommentId
import com.example.mykku.feed.domain.vo.FeedId
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface FeedCommentRepository {
    fun save(feedCommentJpaEntity: FeedCommentJpaEntity): FeedCommentJpaEntity
    fun findById(id: FeedCommentId): FeedCommentJpaEntity?
    fun findByIdOrThrow(id: FeedCommentId): FeedCommentJpaEntity
    fun findByFeedAndParentCommentIsNull(feed: FeedJpaEntity, pageable: Pageable): Page<FeedCommentJpaEntity>
    fun findByParentComment(parentComment: FeedCommentJpaEntity): List<FeedCommentJpaEntity>
    fun findByParentCommentIn(parentComments: List<FeedCommentJpaEntity>): List<FeedCommentJpaEntity>
    fun countByFeed(feed: FeedJpaEntity): Long
    fun findIdsByFeed(feed: FeedJpaEntity): List<Long>
    fun delete(feedCommentJpaEntity: FeedCommentJpaEntity)
    fun deleteAllByFeed(feed: FeedJpaEntity)
}
