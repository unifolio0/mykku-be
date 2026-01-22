package com.example.mykku.feed.domain

import com.example.mykku.board.adapter.output.persistence.entity.BoardJpaEntity
import com.example.mykku.common.domain.BaseEntity
import com.example.mykku.feed.exception.FeedException
import com.example.mykku.member.domain.Member
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

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
    var board: BoardJpaEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val member: Member,
) : BaseEntity() {
    companion object {
        const val CONTENT_MAX_LENGTH = 1000
        const val IMAGE_MAX_COUNT = 10
        const val TAG_MAX_COUNT = 7
    }

    init {
        if (content.length > CONTENT_MAX_LENGTH) {
            throw FeedException.feedContentTooLong()
        }
    }
}
