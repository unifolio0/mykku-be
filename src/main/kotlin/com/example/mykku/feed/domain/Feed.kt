package com.example.mykku.feed.domain

import com.example.mykku.board.domain.Board
import jakarta.persistence.*

@Entity
class Feed(
    @Column(name = "title")
    val title: String,

    @Column(name = "content")
    val content: String,

    @Column(name = "like_count")
    val likeCount: Int = 0,

    @Column(name = "comment_count")
    val commentCount: Int = 0,

    @ManyToOne
    @JoinColumn(name = "board_id")
    val board: Board,

    @OneToMany(mappedBy = "feed")
    val feedImages: List<FeedImage> = mutableListOf(),

    @OneToMany(mappedBy = "feed")
    val feedComments: List<FeedComment> = mutableListOf(),

    @OneToMany(mappedBy = "feed")
    val feedTags: List<FeedTag> = mutableListOf(),
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0
}
