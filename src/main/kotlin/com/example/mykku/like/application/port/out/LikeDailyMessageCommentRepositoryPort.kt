package com.example.mykku.like.application.port.out

import com.example.mykku.dailymessage.domain.DailyMessageComment
import com.example.mykku.like.domain.LikeDailyMessageComment
import com.example.mykku.member.domain.Member

interface LikeDailyMessageCommentRepositoryPort {
    fun createLikeDailyMessageComment(dailyMessageComment: DailyMessageComment, member: Member): LikeDailyMessageComment
    fun deleteLikeDailyMessageComment(memberId: String, dailyMessageCommentId: Long)
}
