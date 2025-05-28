package com.example.mykku.feed.domain

import jakarta.persistence.*
import java.util.*

@Entity
class FeedImage(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(name = "url")
    var url: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id")
    val feed: Feed,
)
