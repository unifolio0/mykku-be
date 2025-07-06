package com.example.mykku.like.tool

import com.example.mykku.exception.ErrorCode
import com.example.mykku.exception.MykkuException
import com.example.mykku.like.domain.LikeBoard
import com.example.mykku.like.repository.LikeBoardRepository
import org.springframework.stereotype.Component

@Component
class LikeBoardReader(
    private val likeBoardRepository: LikeBoardRepository
) {
    fun validateLikeBoardExists(memberId: String, boardId: Long) {
        if (likeBoardRepository.existsByMemberIdAndBoardId(memberId, boardId)) {
            throw MykkuException(ErrorCode.LIKE_BOARD_ALREADY_LIKED)
        }
    }

    fun validateLikeBoardNotExists(memberId: String, boardId: Long) {
        if (!likeBoardRepository.existsByMemberIdAndBoardId(memberId, boardId)) {
            throw MykkuException(ErrorCode.LIKE_BOARD_NOT_FOUND)
        }
    }

    fun getLikedBoards(memberId: String): List<LikeBoard> {
        return likeBoardRepository.findAllByMemberId(memberId)
    }
}
