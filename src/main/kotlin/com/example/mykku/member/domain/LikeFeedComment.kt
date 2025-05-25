package com.example.mykku.member.domain

import com.example.mykku.common.domain.BaseEntity
import com.example.mykku.feed.domain.FeedComment
import jakarta.persistence.*
import java.util.*

@Entity
class LikeFeedComment(
    @Id
    val id: UUID = UUID.randomUUID(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val member: Member,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_comment_id")
    val feedComment: FeedComment,
) : BaseEntity() {
}
