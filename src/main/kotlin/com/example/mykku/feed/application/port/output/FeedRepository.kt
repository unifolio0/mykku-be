package com.example.mykku.feed.application.port.output

import com.example.mykku.feed.adapter.output.persistence.entity.FeedJpaEntity
import com.example.mykku.feed.domain.entity.Feed
import com.example.mykku.feed.domain.vo.FeedId
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface FeedRepository {
    fun save(feedJpaEntity: FeedJpaEntity): FeedJpaEntity
    fun findById(id: FeedId): FeedJpaEntity?
    fun findByIdOrThrow(id: FeedId): FeedJpaEntity
    fun findByBoardId(boardId: Long, pageable: Pageable): Page<FeedJpaEntity>
    fun findPopularFeedsByBoardId(boardId: Long, limit: Int, daysAgo: Int): List<FeedJpaEntity>
    fun delete(feedJpaEntity: FeedJpaEntity)
}
