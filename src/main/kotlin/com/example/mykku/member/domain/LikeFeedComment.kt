package com.example.mykku.member.domain

import com.example.mykku.feed.domain.FeedComment
import jakarta.persistence.*

@Entity
class LikeFeedComment(
    @ManyToOne
    @JoinColumn(name = "member_id")
    val member: Member,

    @ManyToOne
    @JoinColumn(name = "feed_comment_id")
    val feedComment: FeedComment,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0
}
