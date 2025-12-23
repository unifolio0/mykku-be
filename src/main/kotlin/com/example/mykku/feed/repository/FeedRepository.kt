package com.example.mykku.feed.repository

import com.example.mykku.board.domain.Board
import com.example.mykku.feed.domain.Feed
import com.example.mykku.member.domain.Member
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface FeedRepository : JpaRepository<Feed, Long> {
    fun findAllByMemberIn(members: List<Member>): List<Feed>
    fun findByMemberId(memberId: String): List<Feed>
    
    @Query("""
        SELECT f FROM Feed f 
        WHERE f.member IN :members 
        ORDER BY f.createdAt DESC
    """)
    fun findAllByMemberInOrderByCreatedAtDesc(
        @Param("members") members: List<Member>,
        pageable: Pageable
    ): Page<Feed>
    
    @Query("""
        SELECT f FROM Feed f 
        WHERE f.board = :board 
        ORDER BY f.createdAt DESC
    """)
    fun findAllByBoardOrderByCreatedAtDesc(
        @Param("board") board: Board,
        pageable: Pageable
    ): Page<Feed>
}
