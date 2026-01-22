package com.example.mykku.feed.adapter.output.persistence

import com.example.mykku.feed.adapter.output.persistence.entity.FeedCommentJpaEntity
import com.example.mykku.feed.adapter.output.persistence.entity.FeedJpaEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface FeedCommentJpaRepository : JpaRepository<FeedCommentJpaEntity, Long> {
    @Query(
        value = "SELECT fc FROM FeedCommentJpaEntity fc JOIN FETCH fc.member WHERE fc.feed = :feed AND fc.parentComment IS NULL ORDER BY fc.createdAt DESC",
        countQuery = "SELECT COUNT(fc) FROM FeedCommentJpaEntity fc WHERE fc.feed = :feed AND fc.parentComment IS NULL"
    )
    fun findByFeedAndParentCommentIsNull(@Param("feed") feed: FeedJpaEntity, pageable: Pageable): Page<FeedCommentJpaEntity>

    @Query("SELECT fc FROM FeedCommentJpaEntity fc JOIN FETCH fc.member WHERE fc.parentComment = :parentComment ORDER BY fc.createdAt ASC")
    fun findByParentComment(@Param("parentComment") parentComment: FeedCommentJpaEntity): List<FeedCommentJpaEntity>

    @Query("SELECT fc FROM FeedCommentJpaEntity fc JOIN FETCH fc.member WHERE fc.parentComment IN :parentComments ORDER BY fc.parentComment.id, fc.createdAt ASC")
    fun findByParentCommentIn(@Param("parentComments") parentComments: List<FeedCommentJpaEntity>): List<FeedCommentJpaEntity>

    fun countByFeed(feed: FeedJpaEntity): Long

    fun findAllByFeed(feed: FeedJpaEntity): List<FeedCommentJpaEntity>

    @Query("SELECT fc.id FROM FeedCommentJpaEntity fc WHERE fc.feed = :feed")
    fun findIdsByFeed(@Param("feed") feed: FeedJpaEntity): List<Long>

    fun deleteAllByFeed(feed: FeedJpaEntity)
}
