package com.example.mykku.like.domain.model

import com.example.mykku.dailymessage.domain.model.DailyMessageCommentId
import com.example.mykku.member.domain.model.MemberId
import java.time.Instant

class LikeDailyMessageCommentDomain private constructor(
    val id: LikeDailyMessageCommentId?,
    val memberId: MemberId,
    val dailyMessageCommentId: DailyMessageCommentId,
    val createdAt: Instant
) {
    companion object {
        fun create(
            memberId: MemberId,
            dailyMessageCommentId: DailyMessageCommentId
        ): LikeDailyMessageCommentDomain {
            return LikeDailyMessageCommentDomain(
                id = null,
                memberId = memberId,
                dailyMessageCommentId = dailyMessageCommentId,
                createdAt = Instant.now()
            )
        }

        fun reconstitute(
            id: LikeDailyMessageCommentId,
            memberId: MemberId,
            dailyMessageCommentId: DailyMessageCommentId,
            createdAt: Instant
        ): LikeDailyMessageCommentDomain {
            return LikeDailyMessageCommentDomain(
                id = id,
                memberId = memberId,
                dailyMessageCommentId = dailyMessageCommentId,
                createdAt = createdAt
            )
        }
    }
}
