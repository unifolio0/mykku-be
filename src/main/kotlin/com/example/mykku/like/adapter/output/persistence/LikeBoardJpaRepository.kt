package com.example.mykku.like.adapter.output.persistence

import com.example.mykku.like.adapter.output.persistence.entity.LikeBoardJpaEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LikeBoardJpaRepository : JpaRepository<LikeBoardJpaEntity, Long> {
    @EntityGraph(attributePaths = ["member", "board"])
    fun findAllByMemberId(memberId: Long): List<LikeBoardJpaEntity>

    @EntityGraph(attributePaths = ["member", "board"])
    fun findAllByMemberId(memberId: Long, pageable: Pageable): Page<LikeBoardJpaEntity>

    fun existsByMemberIdAndBoardId(memberId: Long, boardId: Long): Boolean
    fun deleteByMemberIdAndBoardId(memberId: Long, boardId: Long)
}
