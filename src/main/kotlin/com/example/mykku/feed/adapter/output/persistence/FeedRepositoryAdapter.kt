package com.example.mykku.feed.adapter.output.persistence

import com.example.mykku.board.adapter.output.persistence.BoardJpaRepository
import com.example.mykku.feed.adapter.output.persistence.entity.FeedJpaEntity
import com.example.mykku.feed.application.port.output.FeedRepository
import com.example.mykku.feed.domain.vo.FeedId
import com.example.mykku.feed.exception.FeedException
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class FeedRepositoryAdapter(
    private val feedJpaRepository: FeedJpaRepository,
    private val boardJpaRepository: BoardJpaRepository
) : FeedRepository {

    companion object {
        const val DEFAULT_POPULAR_FEEDS_LIMIT = 3
        const val DEFAULT_POPULAR_FEEDS_DAYS_AGO = 7
    }

    override fun save(feedJpaEntity: FeedJpaEntity): FeedJpaEntity {
        return feedJpaRepository.save(feedJpaEntity)
    }

    override fun findById(id: FeedId): FeedJpaEntity? {
        return feedJpaRepository.findById(id.value).orElse(null)
    }

    override fun findByIdOrThrow(id: FeedId): FeedJpaEntity {
        return feedJpaRepository.findById(id.value)
            .orElseThrow { FeedException.feedNotFound() }
    }

    override fun findByBoardId(boardId: Long, pageable: Pageable): Page<FeedJpaEntity> {
        val board = boardJpaRepository.findById(boardId)
            .orElseThrow { throw IllegalArgumentException("Board not found") }
        return feedJpaRepository.findAllByBoardOrderByCreatedAtDesc(board, pageable)
    }

    override fun findPopularFeedsByBoardId(boardId: Long, limit: Int, daysAgo: Int): List<FeedJpaEntity> {
        val board = boardJpaRepository.findById(boardId)
            .orElseThrow { throw IllegalArgumentException("Board not found") }
        val since = LocalDateTime.now().minusDays(daysAgo.toLong())
        val pageable = PageRequest.of(0, limit)
        return feedJpaRepository.findPopularFeedsByBoardSince(board, since, pageable)
    }

    override fun delete(feedJpaEntity: FeedJpaEntity) {
        feedJpaRepository.delete(feedJpaEntity)
    }
}
