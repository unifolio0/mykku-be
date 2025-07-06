package com.example.mykku.like.tool

import com.example.mykku.exception.ErrorCode
import com.example.mykku.exception.MykkuException
import com.example.mykku.like.repository.LikeDailyMessageCommentRepository
import org.springframework.stereotype.Component

@Component
class LikeDailyMessageCommentReader(
    private val likeDailyMessageCommentRepository: LikeDailyMessageCommentRepository
) {
    fun validateLikeDailyMessageCommentExists(memberId: String, dailyMessageCommentId: Long) {
        if (likeDailyMessageCommentRepository.existsByMemberIdAndDailyMessageCommentId(
                memberId,
                dailyMessageCommentId
            )
        ) {
            throw MykkuException(ErrorCode.LIKE_DAILY_MESSAGE_COMMENT_ALREADY_LIKED)
        }
    }

    fun validateLikeDailyMessageCommentNotExists(memberId: String, dailyMessageCommentId: Long) {
        if (!likeDailyMessageCommentRepository.existsByMemberIdAndDailyMessageCommentId(
                memberId,
                dailyMessageCommentId
            )
        ) {
            throw MykkuException(ErrorCode.LIKE_DAILY_MESSAGE_COMMENT_NOT_FOUND)
        }
    }
}
