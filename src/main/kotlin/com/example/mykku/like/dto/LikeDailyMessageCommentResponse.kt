package com.example.mykku.like.dto

import com.example.mykku.like.domain.LikeDailyMessageComment

data class LikeDailyMessageCommentResponse(
    val id: Long,
    val memberId: String,
    val dailyMessageCommentId: Long,
) {
    constructor(likeDailyMessageComment: LikeDailyMessageComment) : this(
        id = likeDailyMessageComment.id!!,
        memberId = likeDailyMessageComment.member.id,
        dailyMessageCommentId = likeDailyMessageComment.dailyMessageComment.id!!
    )
}
