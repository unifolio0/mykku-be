package com.example.mykku.feed.adapter.output.persistence

import com.example.mykku.board.adapter.output.persistence.entity.BoardJpaEntity
import com.example.mykku.feed.adapter.output.persistence.entity.FeedJpaEntity
import com.example.mykku.member.adapter.output.persistence.entity.MemberJpaEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface FeedJpaRepository : JpaRepository<FeedJpaEntity, Long> {
    fun findAllByMemberIn(members: List<MemberJpaEntity>): List<FeedJpaEntity>

    @Query("""
        SELECT f FROM FeedJpaEntity f
        WHERE f.member IN :members
        ORDER BY f.createdAt DESC
    """)
    fun findAllByMemberInOrderByCreatedAtDesc(
        @Param("members") members: List<MemberJpaEntity>,
        pageable: Pageable
    ): Page<FeedJpaEntity>

    @Query("""
        SELECT f FROM FeedJpaEntity f
        WHERE f.board = :board
        ORDER BY f.createdAt DESC
    """)
    fun findAllByBoardOrderByCreatedAtDesc(
        @Param("board") board: BoardJpaEntity,
        pageable: Pageable
    ): Page<FeedJpaEntity>

    fun findAllByIdIn(ids: List<Long>): List<FeedJpaEntity>

    @Query("""
        SELECT f FROM FeedJpaEntity f
        WHERE f.member.id = :memberId
        ORDER BY f.createdAt DESC
    """)
    fun findAllByMemberIdOrderByCreatedAtDesc(
        @Param("memberId") memberId: Long,
        pageable: Pageable
    ): Page<FeedJpaEntity>

    @Query("""
        SELECT f FROM FeedJpaEntity f
        WHERE f.board = :board
        AND f.createdAt >= :since
        ORDER BY f.likeCount DESC
    """)
    fun findPopularFeedsByBoardSince(
        @Param("board") board: BoardJpaEntity,
        @Param("since") since: LocalDateTime,
        pageable: Pageable
    ): List<FeedJpaEntity>
}
