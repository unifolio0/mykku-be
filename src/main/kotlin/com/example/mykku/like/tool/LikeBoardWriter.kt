package com.example.mykku.like.tool

import com.example.mykku.board.domain.Board
import com.example.mykku.like.domain.LikeBoard
import com.example.mykku.like.repository.LikeBoardRepository
import com.example.mykku.member.domain.Member
import org.springframework.stereotype.Component

@Component
class LikeBoardWriter(
    private val likeBoardRepository: LikeBoardRepository
) {
    fun createLikeBoard(
        member: Member,
        board: Board
    ): LikeBoard {
        val likeBoard = LikeBoard(
            member = member,
            board = board
        )
        return likeBoardRepository.save(likeBoard)
    }

    fun deleteLikeBoard(memberId: String, boardId: Long) {
        likeBoardRepository.deleteByMemberIdAndBoardId(memberId, boardId)
    }
}
