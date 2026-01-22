package com.example.mykku.like.adapter.output.persistence.entity

import com.example.mykku.common.domain.BaseEntity
import com.example.mykku.feed.domain.FeedComment
import com.example.mykku.like.domain.entity.LikeFeedCommentEntity
import com.example.mykku.member.domain.Member
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "like_feed_comment")
class LikeFeedCommentJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val member: Member,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_comment_id")
    val feedComment: FeedComment
) : BaseEntity() {

    fun toDomain(): LikeFeedCommentEntity {
        return LikeFeedCommentEntity.reconstitute(
            id = this.id!!,
            memberId = this.member.id,
            feedCommentId = this.feedComment.id!!,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt
        )
    }

    companion object {
        fun fromDomain(
            domain: LikeFeedCommentEntity,
            member: Member,
            feedComment: FeedComment
        ): LikeFeedCommentJpaEntity {
            return LikeFeedCommentJpaEntity(
                id = domain.id?.value,
                member = member,
                feedComment = feedComment
            )
        }
    }
}
