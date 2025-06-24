package com.example.mykku.feed.domain

import com.example.mykku.board.domain.Board
import com.example.mykku.common.domain.BaseEntity
import com.example.mykku.member.domain.Member
import com.example.mykku.exception.ErrorCode
import com.example.mykku.exception.MykkuException
import jakarta.persistence.*

@Entity
class Feed(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "title")
    var title: String,

    @Column(name = "content")
    var content: String,

    @Column(name = "like_count")
    var likeCount: Int = 0,

    @Column(name = "comment_count")
    var commentCount: Int = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    val board: Board,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val member: Member,

    @OneToMany(mappedBy = "feed", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val feedImages: MutableList<FeedImage> = mutableListOf(),

    @OneToMany(mappedBy = "feed", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val feedComments: MutableList<FeedComment> = mutableListOf(),

    @OneToMany(mappedBy = "feed", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val feedTags: MutableList<FeedTag> = mutableListOf(),
) : BaseEntity() {
    companion object {
        const val CONTENT_MAX_LENGTH = 1000
        const val IMAGE_MAX_COUNT = 10
        const val TAG_MAX_COUNT = 7
    }

    init {
        if (content.length > CONTENT_MAX_LENGTH) {
            throw MykkuException(ErrorCode.FEED_CONTENT_TOO_LONG)
        }
        if (feedImages.size > IMAGE_MAX_COUNT) {
            throw MykkuException(ErrorCode.FEED_IMAGE_LIMIT_EXCEEDED)
        }
        if (feedTags.size > TAG_MAX_COUNT) {
            throw MykkuException(ErrorCode.FEED_TAG_LIMIT_EXCEEDED)
        }
    }
}
