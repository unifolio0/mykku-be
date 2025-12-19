package com.example.mykku.like.infrastructure.adapter

import com.example.mykku.like.application.port.out.LikeBoardQueryPort
import com.example.mykku.like.domain.LikeBoard
import com.example.mykku.like.exception.LikeException
import com.example.mykku.like.repository.LikeBoardRepository
import org.springframework.stereotype.Component

@Component
class LikeBoardQueryAdapter(
    private val likeBoardRepository: LikeBoardRepository
) : LikeBoardQueryPort {

    override fun validateLikeBoardNotExists(memberId: String, boardId: Long) {
        if (likeBoardRepository.existsByMemberIdAndBoardId(memberId, boardId)) {
            throw LikeException.likeBoardAlreadyLiked()
        }
    }

    override fun validateLikeBoardExists(memberId: String, boardId: Long) {
        if (!likeBoardRepository.existsByMemberIdAndBoardId(memberId, boardId)) {
            throw LikeException.likeBoardNotFound()
        }
    }

    override fun getLikedBoards(memberId: String): List<LikeBoard> {
        return likeBoardRepository.findAllByMemberId(memberId)
    }
}
