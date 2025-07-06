package com.example.mykku.like.domain

import com.example.mykku.common.domain.BaseEntity
import com.example.mykku.feed.domain.FeedComment
import com.example.mykku.member.domain.Member
import jakarta.persistence.*

@Entity
class LikeFeedComment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val member: Member,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_comment_id")
    val feedComment: FeedComment,
) : BaseEntity() {
}
