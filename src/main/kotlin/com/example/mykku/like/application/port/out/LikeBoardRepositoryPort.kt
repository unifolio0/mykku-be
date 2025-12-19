package com.example.mykku.like.application.port.out

import com.example.mykku.board.domain.Board
import com.example.mykku.like.domain.LikeBoard
import com.example.mykku.member.domain.Member

interface LikeBoardRepositoryPort {
    fun createLikeBoard(member: Member, board: Board): LikeBoard
    fun deleteLikeBoard(memberId: String, boardId: Long)
}
