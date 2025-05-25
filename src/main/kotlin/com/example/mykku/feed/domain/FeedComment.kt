package com.example.mykku.feed.domain

import com.example.mykku.common.domain.BaseEntity
import com.example.mykku.member.domain.Member
import jakarta.persistence.*
import java.util.*

@Entity
class FeedComment(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(name = "content", nullable = false)
    var content: String,

    @Column(name = "like_count", nullable = false)
    var likeCount: Int = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id")
    val feed: Feed,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    val parentComment: FeedComment? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val member: Member,
) : BaseEntity() {
}
