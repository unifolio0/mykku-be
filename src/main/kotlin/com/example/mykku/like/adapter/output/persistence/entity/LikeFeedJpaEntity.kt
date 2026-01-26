package com.example.mykku.like.adapter.output.persistence.entity

import com.example.mykku.common.adapter.persistence.BaseJpaEntity
import com.example.mykku.feed.adapter.output.persistence.entity.FeedJpaEntity
import com.example.mykku.like.domain.entity.LikeFeedEntity
import com.example.mykku.member.adapter.output.persistence.entity.MemberJpaEntity
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "like_feed")
class LikeFeedJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val member: MemberJpaEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id")
    val feed: FeedJpaEntity
) : BaseJpaEntity() {

    fun toDomain(): LikeFeedEntity {
        return LikeFeedEntity.reconstitute(
            id = this.id!!,
            memberId = this.member.id,
            feedId = this.feed.id!!,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt
        )
    }

    companion object {
        fun fromDomain(
            domain: LikeFeedEntity,
            member: MemberJpaEntity,
            feed: FeedJpaEntity
        ): LikeFeedJpaEntity {
            return LikeFeedJpaEntity(
                id = domain.id?.value,
                member = member,
                feed = feed
            )
        }
    }
}
