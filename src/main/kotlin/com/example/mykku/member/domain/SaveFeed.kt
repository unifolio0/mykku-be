package com.example.mykku.member.domain

import com.example.mykku.common.domain.BaseEntity
import com.example.mykku.feed.domain.Feed
import jakarta.persistence.*

@Entity
class SaveFeed(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val member: Member,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id")
    val feed: Feed,
) : BaseEntity() {
}
