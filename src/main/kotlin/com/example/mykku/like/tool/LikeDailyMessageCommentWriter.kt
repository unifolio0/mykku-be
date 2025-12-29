package com.example.mykku.like.tool

import com.example.mykku.dailymessage.domain.DailyMessageComment
import com.example.mykku.like.domain.LikeDailyMessageComment
import com.example.mykku.like.repository.LikeDailyMessageCommentRepository
import com.example.mykku.member.domain.Member
import org.springframework.stereotype.Component

@Component
class LikeDailyMessageCommentWriter(
    private val likeDailyMessageCommentRepository: LikeDailyMessageCommentRepository
) {
    fun createLikeDailyMessageComment(
        dailyMessageComment: DailyMessageComment,
        member: Member
    ): LikeDailyMessageComment {
        val likeDailyMessageComment = LikeDailyMessageComment(
            member = member,
            dailyMessageComment = dailyMessageComment
        )
        return likeDailyMessageCommentRepository.save(likeDailyMessageComment)
    }

    fun deleteLikeDailyMessageComment(memberId: String, dailyMessageCommentId: Long) {
        likeDailyMessageCommentRepository.deleteByMemberIdAndDailyMessageCommentId(memberId, dailyMessageCommentId)
    }
}
