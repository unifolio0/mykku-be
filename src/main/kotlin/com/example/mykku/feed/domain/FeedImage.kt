package com.example.mykku.feed.domain

import jakarta.persistence.*

@Entity
class FeedImage(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "url", nullable = false)
    var url: String,

    @ManyToOne
    @JoinColumn(name = "feed_id")
    val feed: Feed,
) {
}
