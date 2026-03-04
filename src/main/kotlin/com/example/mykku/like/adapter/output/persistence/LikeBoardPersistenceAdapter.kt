package com.example.mykku.like.adapter.output.persistence

import com.example.mykku.board.adapter.output.persistence.BoardJpaRepository
import com.example.mykku.board.exception.BoardException
import com.example.mykku.like.adapter.output.persistence.entity.LikeBoardJpaEntity
import com.example.mykku.like.application.dto.LikeBoardInfoResult
import com.example.mykku.like.application.port.output.LikeBoardPort
import com.example.mykku.like.domain.entity.LikeBoardEntity
import com.example.mykku.member.adapter.output.persistence.MemberJpaRepository
import com.example.mykku.member.exception.MemberException
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
            ?: throw MemberException.memberNotFound()
        val board = boardJpaRepository.findByIdOrNull(likeBoard.boardId)
            ?: throw BoardException.boardNotFound()

        val jpaEntity = LikeBoardJpaEntity.fromDomain(likeBoard, member, board)
        return likeBoardJpaRepository.save(jpaEntity).toDomain()
    }

    override fun existsByMemberIdAndBoardId(memberId: Long, boardId: Long): Boolean {
        return likeBoardJpaRepository.existsByMemberIdAndBoardId(memberId, boardId)
    }

    override fun deleteByMemberIdAndBoardId(memberId: Long, boardId: Long) {
        likeBoardJpaRepository.deleteByMemberIdAndBoardId(memberId, boardId)
    }

    override fun findAllByMemberId(memberId: Long): List<LikeBoardEntity> {
        return likeBoardJpaRepository.findAllByMemberId(memberId)
            .map { it.toDomain() }
    }

    override fun findAllByMemberIdWithBoardInfo(memberId: Long, pageable: Pageable): Page<LikeBoardInfoResult> {
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
