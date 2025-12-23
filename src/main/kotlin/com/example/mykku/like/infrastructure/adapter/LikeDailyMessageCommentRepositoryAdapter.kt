package com.example.mykku.like.infrastructure.adapter

import com.example.mykku.dailymessage.domain.DailyMessageComment
import com.example.mykku.like.application.port.out.LikeDailyMessageCommentRepositoryPort
import com.example.mykku.like.domain.LikeDailyMessageComment
import com.example.mykku.like.repository.LikeDailyMessageCommentRepository
import com.example.mykku.member.domain.Member
import org.springframework.stereotype.Component

@Component
class LikeDailyMessageCommentRepositoryAdapter(
    private val likeDailyMessageCommentRepository: LikeDailyMessageCommentRepository
) : LikeDailyMessageCommentRepositoryPort {

    override fun createLikeDailyMessageComment(
        dailyMessageComment: DailyMessageComment,
        member: Member
    ): LikeDailyMessageComment {
        val likeDailyMessageComment = LikeDailyMessageComment(
            member = member,
            dailyMessageComment = dailyMessageComment
        )
        return likeDailyMessageCommentRepository.save(likeDailyMessageComment)
    }

    override fun deleteLikeDailyMessageComment(memberId: String, dailyMessageCommentId: Long) {
        likeDailyMessageCommentRepository.deleteByMemberIdAndDailyMessageCommentId(memberId, dailyMessageCommentId)
    }
}
