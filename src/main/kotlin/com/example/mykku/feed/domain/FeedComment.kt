package com.example.mykku.feed.domain

import jakarta.persistence.*

@Entity
class FeedComment(
    @Column(name = "content")
    val content: String,

    @Column(name = "like_count")
    val likeCount: Int = 0,

    @ManyToOne
    @JoinColumn(name = "feed_id")
    val feed: Feed,

    @ManyToOne
    @JoinColumn(name = "parent_comment_id")
    val parentComment: FeedComment? = null,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0
}
