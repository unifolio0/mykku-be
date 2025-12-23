package com.example.mykku.like.domain.model

import com.example.mykku.feed.domain.model.FeedCommentId
import com.example.mykku.member.domain.model.MemberId
import java.time.Instant

class LikeFeedCommentDomain private constructor(
    val id: LikeFeedCommentId?,
    val memberId: MemberId,
    val feedCommentId: FeedCommentId,
    val createdAt: Instant
) {
    companion object {
        fun create(
            memberId: MemberId,
            feedCommentId: FeedCommentId
        ): LikeFeedCommentDomain {
            return LikeFeedCommentDomain(
                id = null,
                memberId = memberId,
                feedCommentId = feedCommentId,
                createdAt = Instant.now()
            )
        }

        fun reconstitute(
            id: LikeFeedCommentId,
            memberId: MemberId,
            feedCommentId: FeedCommentId,
            createdAt: Instant
        ): LikeFeedCommentDomain {
            return LikeFeedCommentDomain(
                id = id,
                memberId = memberId,
                feedCommentId = feedCommentId,
                createdAt = createdAt
            )
        }
    }
}
