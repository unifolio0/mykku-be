package com.example.mykku.feed.application.port.output

import com.example.mykku.feed.domain.entity.Feed
import com.example.mykku.feed.domain.vo.FeedId
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface FeedRepository {
    fun save(feed: Feed, boardId: Long, memberId: String): Feed
    fun update(feed: Feed): Feed
    fun findById(id: FeedId): Feed?
    fun findByIdOrThrow(id: FeedId): Feed
    fun findByBoardId(boardId: Long, pageable: Pageable): Page<Feed>
    fun findPopularFeedsByBoardId(boardId: Long, limit: Int, daysAgo: Int): List<Feed>
    fun delete(feed: Feed)
}
