package com.example.mykku.like.tool

import com.example.mykku.like.exception.LikeException
import com.example.mykku.like.domain.LikeBoard
import com.example.mykku.like.repository.LikeBoardRepository
import org.springframework.stereotype.Component

@Component
class LikeBoardReader(
    private val likeBoardRepository: LikeBoardRepository
) {
    fun validateLikeBoardNotExists(memberId: String, boardId: Long) {
        if (likeBoardRepository.existsByMemberIdAndBoardId(memberId, boardId)) {
            throw LikeException.likeBoardAlreadyLiked()
        }
    }

    fun validateLikeBoardExists(memberId: String, boardId: Long) {
        if (!likeBoardRepository.existsByMemberIdAndBoardId(memberId, boardId)) {
            throw LikeException.likeBoardNotFound()
        }
    }

    fun getLikedBoards(memberId: String): List<LikeBoard> {
        return likeBoardRepository.findAllByMemberId(memberId)
    }
}
