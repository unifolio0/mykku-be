package com.example.mykku.like.tool

import com.example.mykku.like.exception.LikeException
import com.example.mykku.like.repository.LikeDailyMessageCommentRepository
import org.springframework.stereotype.Component

@Component
class LikeDailyMessageCommentReader(
    private val likeDailyMessageCommentRepository: LikeDailyMessageCommentRepository
) {
    fun validateLikeDailyMessageCommentNotExists(memberId: String, dailyMessageCommentId: Long) {
        if (likeDailyMessageCommentRepository.existsByMemberIdAndDailyMessageCommentId(
                memberId,
                dailyMessageCommentId
            )
        ) {
            throw LikeException.likeDailyMessageCommentAlreadyLiked()
        }
    }

    fun validateLikeDailyMessageCommentExists(memberId: String, dailyMessageCommentId: Long) {
        if (!likeDailyMessageCommentRepository.existsByMemberIdAndDailyMessageCommentId(
                memberId,
                dailyMessageCommentId
            )
        ) {
            throw LikeException.likeDailyMessageCommentNotFound()
        }
    }
}
