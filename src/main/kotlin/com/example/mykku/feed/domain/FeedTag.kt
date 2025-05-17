package com.example.mykku.feed.domain

import jakarta.persistence.*

@Entity
class FeedTag(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "feed_id")
    val feed: Feed,

    @ManyToOne
    @JoinColumn(name = "tag_id")
    val tag: Tag,
) {
}
