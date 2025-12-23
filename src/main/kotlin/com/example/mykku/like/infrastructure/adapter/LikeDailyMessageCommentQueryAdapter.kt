package com.example.mykku.like.infrastructure.adapter

import com.example.mykku.like.application.port.out.LikeDailyMessageCommentQueryPort
import com.example.mykku.like.exception.LikeException
import com.example.mykku.like.repository.LikeDailyMessageCommentRepository
import org.springframework.stereotype.Component

@Component
class LikeDailyMessageCommentQueryAdapter(
    private val likeDailyMessageCommentRepository: LikeDailyMessageCommentRepository
) : LikeDailyMessageCommentQueryPort {

    override fun validateLikeDailyMessageCommentNotExists(memberId: String, dailyMessageCommentId: Long) {
        if (likeDailyMessageCommentRepository.existsByMemberIdAndDailyMessageCommentId(memberId, dailyMessageCommentId)) {
            throw LikeException.likeDailyMessageCommentAlreadyLiked()
        }
    }

    override fun validateLikeDailyMessageCommentExists(memberId: String, dailyMessageCommentId: Long) {
        if (!likeDailyMessageCommentRepository.existsByMemberIdAndDailyMessageCommentId(memberId, dailyMessageCommentId)) {
            throw LikeException.likeDailyMessageCommentNotFound()
        }
    }
}
