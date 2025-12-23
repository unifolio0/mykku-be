package com.example.mykku.like.domain.model

import com.example.mykku.feed.domain.model.FeedId
import com.example.mykku.member.domain.model.MemberId
import java.time.Instant

class LikeFeedDomain private constructor(
    val id: LikeFeedId?,
    val memberId: MemberId,
    val feedId: FeedId,
    val createdAt: Instant
) {
    companion object {
        fun create(
            memberId: MemberId,
            feedId: FeedId
        ): LikeFeedDomain {
            return LikeFeedDomain(
                id = null,
                memberId = memberId,
                feedId = feedId,
                createdAt = Instant.now()
            )
        }

        fun reconstitute(
            id: LikeFeedId,
            memberId: MemberId,
            feedId: FeedId,
            createdAt: Instant
        ): LikeFeedDomain {
            return LikeFeedDomain(
                id = id,
                memberId = memberId,
                feedId = feedId,
                createdAt = createdAt
            )
        }
    }
}
