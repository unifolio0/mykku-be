package com.example.mykku.like.application.port.out

interface LikeDailyMessageCommentQueryPort {
    fun validateLikeDailyMessageCommentNotExists(memberId: String, dailyMessageCommentId: Long)
    fun validateLikeDailyMessageCommentExists(memberId: String, dailyMessageCommentId: Long)
}
