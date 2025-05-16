package com.example.mykku.member.domain

import com.example.mykku.feed.domain.Feed
import jakarta.persistence.*

@Entity
class SaveFeed(
    @ManyToOne
    @JoinColumn(name = "member_id")
    val member: Member,

    @ManyToOne
    @JoinColumn(name = "feed_id")
    val feed: Feed,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0
}
