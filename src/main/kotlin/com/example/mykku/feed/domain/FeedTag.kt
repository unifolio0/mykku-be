package com.example.mykku.feed.domain

import jakarta.persistence.*

@Entity
class FeedTag(
    @ManyToOne
    @JoinColumn(name = "feed_id")
    val feed: Feed,

    @ManyToOne
    @JoinColumn(name = "tag_id")
    val tag: Tag,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0
}
