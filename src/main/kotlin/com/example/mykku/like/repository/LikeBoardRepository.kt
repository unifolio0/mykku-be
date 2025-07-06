package com.example.mykku.like.repository

import com.example.mykku.like.domain.LikeBoard
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LikeBoardRepository : JpaRepository<LikeBoard, Long> {
    fun findAllByMemberId(memberId: String): List<LikeBoard>
    fun existsByMemberIdAndBoardId(memberId: String, boardId: Long): Boolean
    fun deleteByMemberIdAndBoardId(memberId: String, boardId: Long)
}
