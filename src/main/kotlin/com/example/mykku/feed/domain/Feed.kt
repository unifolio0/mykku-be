package com.example.mykku.feed.domain

import com.example.mykku.board.domain.Board
import com.example.mykku.member.domain.Member
import jakarta.persistence.*

@Entity
class Feed(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "title")
    var title: String,

    @Column(name = "content")
    var content: String,

    @Column(name = "like_count")
    var likeCount: Int = 0,

    @Column(name = "comment_count")
    var commentCount: Int = 0,

    @ManyToOne
    @JoinColumn(name = "board_id")
    val board: Board,

    @ManyToOne
    @JoinColumn(name = "member_id")
    val member: Member,

    @OneToMany(mappedBy = "feed", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val feedImages: MutableList<FeedImage> = mutableListOf(),

    @OneToMany(mappedBy = "feed", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val feedComments: MutableList<FeedComment> = mutableListOf(),

    @OneToMany(mappedBy = "feed", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val feedTags: MutableList<FeedTag> = mutableListOf(),
) {
}
