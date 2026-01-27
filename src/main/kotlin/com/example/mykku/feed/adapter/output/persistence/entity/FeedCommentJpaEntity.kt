package com.example.mykku.feed.adapter.output.persistence.entity

import com.example.mykku.common.adapter.persistence.BaseJpaEntity
import com.example.mykku.feed.domain.entity.FeedComment
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
@Table(name = "feed_comment")
class FeedCommentJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "content")
    var content: String,

    @Column(name = "like_count")
    var likeCount: Int = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id")
    val feed: FeedJpaEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    val parentComment: FeedCommentJpaEntity? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val member: MemberJpaEntity
) : BaseJpaEntity() {

    companion object {
        const val CONTENT_MAX_LENGTH = 1000

        fun fromDomain(
            feedComment: FeedComment,
            feed: FeedJpaEntity,
            member: MemberJpaEntity,
            parentComment: FeedCommentJpaEntity? = null
        ): FeedCommentJpaEntity {
            return FeedCommentJpaEntity(
                id = feedComment.id?.value,
                content = feedComment.content,
                likeCount = feedComment.likeCount,
                feed = feed,
                parentComment = parentComment,
                member = member
            )
        }
    }

    init {
        if (content.length > CONTENT_MAX_LENGTH) {
            throw FeedException.feedCommentContentTooLong()
        }
    }

    fun toDomain(): FeedComment = FeedComment.reconstitute(
        id = id!!,
        content = content,
        likeCount = likeCount,
        feedId = feed.id!!,
        parentCommentId = parentComment?.id,
        memberId = member.id,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    fun updateContent(newContent: String) {
        if (newContent.length > CONTENT_MAX_LENGTH) {
            throw FeedException.feedCommentContentTooLong()
        }
        this.content = newContent
    }
}
