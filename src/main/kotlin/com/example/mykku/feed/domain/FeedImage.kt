package com.example.mykku.feed.domain

import jakarta.persistence.*

@Entity
class FeedImage(
    @Column(name = "url")
    val url: String,

    @ManyToOne
    @JoinColumn(name = "feed_id")
    val feed: Feed,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0
}
