package com.example.mykku.like.adapter.output.persistence.entity

import com.example.mykku.common.domain.BaseEntity
import com.example.mykku.feed.domain.Feed
import com.example.mykku.like.domain.entity.LikeFeedEntity
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
@Table(name = "like_feed")
class LikeFeedJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val member: Member,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id")
    val feed: Feed
) : BaseEntity() {

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
            member: Member,
            feed: Feed
        ): LikeFeedJpaEntity {
            return LikeFeedJpaEntity(
                id = domain.id?.value,
                member = member,
                feed = feed
            )
        }
    }
}
