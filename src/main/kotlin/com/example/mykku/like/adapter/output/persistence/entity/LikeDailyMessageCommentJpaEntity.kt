package com.example.mykku.like.adapter.output.persistence.entity

import com.example.mykku.common.adapter.persistence.BaseJpaEntity
import com.example.mykku.dailymessage.adapter.output.persistence.entity.DailyMessageCommentJpaEntity
import com.example.mykku.like.domain.entity.LikeDailyMessageCommentEntity
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
@Table(name = "like_daily_message_comment")
class LikeDailyMessageCommentJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val member: MemberJpaEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "daily_message_comment_id")
    val dailyMessageComment: DailyMessageCommentJpaEntity
) : BaseJpaEntity() {

    fun toDomain(): LikeDailyMessageCommentEntity {
        return LikeDailyMessageCommentEntity.reconstitute(
            id = this.id!!,
            memberId = this.member.id,
            dailyMessageCommentId = this.dailyMessageComment.id!!,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt
        )
    }

    companion object {
        fun fromDomain(
            domain: LikeDailyMessageCommentEntity,
            member: MemberJpaEntity,
            dailyMessageComment: DailyMessageCommentJpaEntity
        ): LikeDailyMessageCommentJpaEntity {
            return LikeDailyMessageCommentJpaEntity(
                id = domain.id?.value,
                member = member,
                dailyMessageComment = dailyMessageComment
            )
        }
    }
}
