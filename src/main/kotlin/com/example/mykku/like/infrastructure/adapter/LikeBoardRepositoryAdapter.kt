package com.example.mykku.like.infrastructure.adapter

import com.example.mykku.board.domain.Board
import com.example.mykku.like.application.port.out.LikeBoardRepositoryPort
import com.example.mykku.like.domain.LikeBoard
import com.example.mykku.like.repository.LikeBoardRepository
import com.example.mykku.member.domain.Member
import org.springframework.stereotype.Component

@Component
class LikeBoardRepositoryAdapter(
    private val likeBoardRepository: LikeBoardRepository
) : LikeBoardRepositoryPort {

    override fun createLikeBoard(member: Member, board: Board): LikeBoard {
        val likeBoard = LikeBoard(
            member = member,
            board = board
        )
        return likeBoardRepository.save(likeBoard)
    }

    override fun deleteLikeBoard(memberId: String, boardId: Long) {
        likeBoardRepository.deleteByMemberIdAndBoardId(memberId, boardId)
    }
}
