package com.example.mykku.feed.adapter.output.persistence.entity

import com.example.mykku.board.adapter.output.persistence.entity.BoardJpaEntity
import com.example.mykku.common.adapter.persistence.BaseJpaEntity
import com.example.mykku.feed.domain.entity.Feed
import com.example.mykku.feed.exception.FeedException
import com.example.mykku.member.adapter.output.persistence.entity.MemberJpaEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "feed")
class FeedJpaEntity(
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
    val member: MemberJpaEntity? = null
) : BaseJpaEntity() {

    companion object {
        const val CONTENT_MAX_LENGTH = 1000
        const val IMAGE_MAX_COUNT = 10
        const val TAG_MAX_COUNT = 7

        fun fromDomain(
            feed: Feed,
            board: BoardJpaEntity,
            member: MemberJpaEntity
        ): FeedJpaEntity {
            return FeedJpaEntity(
                id = feed.id?.value,
                title = feed.title,
                content = feed.content,
                likeCount = feed.likeCount,
                commentCount = feed.commentCount,
                board = board,
                member = member
            )
        }
    }

    init {
        if (content.length > CONTENT_MAX_LENGTH) {
            throw FeedException.feedContentTooLong()
        }
    }

    fun toDomain(): Feed = Feed.reconstitute(
        id = id!!,
        title = title,
        content = content,
        likeCount = likeCount,
        commentCount = commentCount,
        boardId = board.id!!,
        memberId = member?.id,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    fun update(title: String?, content: String?, board: BoardJpaEntity?) {
        title?.let { this.title = it }
        content?.let {
            if (it.length > CONTENT_MAX_LENGTH) {
                throw FeedException.feedContentTooLong()
            }
            this.content = it
        }
        board?.let { this.board = it }
    }
}
