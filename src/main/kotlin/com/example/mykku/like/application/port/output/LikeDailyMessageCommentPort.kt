package com.example.mykku.like.application.port.output

import com.example.mykku.like.domain.entity.LikeDailyMessageCommentEntity

interface LikeDailyMessageCommentPort {
    fun save(likeDailyMessageComment: LikeDailyMessageCommentEntity): LikeDailyMessageCommentEntity
    fun existsByMemberIdAndDailyMessageCommentId(memberId: String, dailyMessageCommentId: Long): Boolean
    fun deleteByMemberIdAndDailyMessageCommentId(memberId: String, dailyMessageCommentId: Long)
}
