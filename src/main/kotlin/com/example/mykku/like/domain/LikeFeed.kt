package com.example.mykku.like.domain

import com.example.mykku.common.domain.BaseEntity
import com.example.mykku.feed.domain.Feed
import com.example.mykku.member.domain.Member
import jakarta.persistence.*

@Entity
class LikeFeed(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val member: Member,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id")
    val feed: Feed,
) : BaseEntity() {
}
