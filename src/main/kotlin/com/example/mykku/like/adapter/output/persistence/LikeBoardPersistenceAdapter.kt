package com.example.mykku.like.adapter.output.persistence

import com.example.mykku.board.adapter.output.persistence.BoardJpaRepository
import com.example.mykku.like.adapter.output.persistence.entity.LikeBoardJpaEntity
import com.example.mykku.like.application.dto.LikeBoardInfoResult
import com.example.mykku.like.application.port.output.LikeBoardPort
import com.example.mykku.like.domain.entity.LikeBoardEntity
import com.example.mykku.member.adapter.output.persistence.MemberJpaRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class LikeBoardPersistenceAdapter(
    private val likeBoardJpaRepository: LikeBoardJpaRepository,
    private val memberJpaRepository: MemberJpaRepository,
    private val boardJpaRepository: BoardJpaRepository
) : LikeBoardPort {

    override fun save(likeBoard: LikeBoardEntity): LikeBoardEntity {
        val member = memberJpaRepository.findByIdOrNull(likeBoard.memberId)
            ?: throw IllegalArgumentException("Member not found: ${likeBoard.memberId}")
        val board = boardJpaRepository.findByIdOrNull(likeBoard.boardId)
            ?: throw IllegalArgumentException("Board not found: ${likeBoard.boardId}")

        val jpaEntity = LikeBoardJpaEntity.fromDomain(likeBoard, member, board)
        return likeBoardJpaRepository.save(jpaEntity).toDomain()
    }

    override fun existsByMemberIdAndBoardId(memberId: String, boardId: Long): Boolean {
        return likeBoardJpaRepository.existsByMemberIdAndBoardId(memberId, boardId)
    }

    override fun deleteByMemberIdAndBoardId(memberId: String, boardId: Long) {
        likeBoardJpaRepository.deleteByMemberIdAndBoardId(memberId, boardId)
    }

    override fun findAllByMemberId(memberId: String): List<LikeBoardEntity> {
        return likeBoardJpaRepository.findAllByMemberId(memberId)
            .map { it.toDomain() }
    }

    override fun findAllByMemberIdWithBoardInfo(memberId: String, pageable: Pageable): Page<LikeBoardInfoResult> {
        return likeBoardJpaRepository.findAllByMemberId(memberId, pageable)
            .map { jpaEntity ->
                LikeBoardInfoResult(
                    id = jpaEntity.id!!,
                    title = jpaEntity.board.title,
                    logo = jpaEntity.board.logo
                )
            }
    }
}
