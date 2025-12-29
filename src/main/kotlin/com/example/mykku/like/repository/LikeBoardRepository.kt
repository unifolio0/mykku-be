package com.example.mykku.like.repository

import com.example.mykku.like.domain.LikeBoard
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LikeBoardRepository : JpaRepository<LikeBoard, Long> {
    @EntityGraph(attributePaths = ["member", "board"])
    fun findAllByMemberId(memberId: String): List<LikeBoard>

    fun existsByMemberIdAndBoardId(memberId: String, boardId: Long): Boolean

    fun deleteByMemberIdAndBoardId(memberId: String, boardId: Long)
}
