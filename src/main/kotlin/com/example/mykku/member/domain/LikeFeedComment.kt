package com.example.mykku.member.domain

import com.example.mykku.common.BaseEntity
import com.example.mykku.feed.domain.FeedComment
import jakarta.persistence.*

@Entity
class LikeFeedComment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val member: Member,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_comment_id")
    val feedComment: FeedComment,
) : BaseEntity() {
}
