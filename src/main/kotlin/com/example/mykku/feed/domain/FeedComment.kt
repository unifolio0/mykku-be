package com.example.mykku.feed.domain

import jakarta.persistence.*

@Entity
class FeedComment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "content")
    var content: String,

    @Column(name = "like_count")
    var likeCount: Int = 0,

    @ManyToOne
    @JoinColumn(name = "feed_id")
    val feed: Feed,

    @ManyToOne
    @JoinColumn(name = "parent_comment_id")
    val parentComment: FeedComment? = null,
) {
}
