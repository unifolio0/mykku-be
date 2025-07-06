package com.example.mykku.member.tool

import com.example.mykku.board.domain.Board
import com.example.mykku.member.domain.LikeBoard
import com.example.mykku.member.domain.Member
import com.example.mykku.member.repository.LikeBoardRepository
import org.springframework.stereotype.Component

@Component
class LikeBoardWriter(
    private val likeBoardRepository: LikeBoardRepository
) {
    fun createLikeBoard(member: Member, board: Board) {
        val likeBoard = LikeBoard(
            member = member,
            board = board
        )
        likeBoardRepository.save(likeBoard)
    }
}
