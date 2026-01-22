package com.example.mykku.contest.adapter.output.persistence.entity

import com.example.mykku.common.domain.BaseEntity
import com.example.mykku.contest.domain.entity.ContestParticipation
import com.example.mykku.contest.domain.vo.ContestId
import com.example.mykku.contest.domain.vo.ContestParticipationId
import com.example.mykku.feed.domain.Feed
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
@Table(name = "contest_participation")
class ContestParticipationJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val member: Member,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contest_id")
    val contest: ContestJpaEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id")
    val feed: Feed
) : BaseEntity() {

    fun toDomain(): ContestParticipation {
        return ContestParticipation.reconstitute(
            id = ContestParticipationId.of(id!!),
            contestId = ContestId.of(contest.id!!),
            feedId = feed.id!!,
            memberId = member.id!!,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    companion object {
        fun fromDomain(
            participation: ContestParticipation,
            contestJpaEntity: ContestJpaEntity,
            member: Member,
            feed: Feed
        ): ContestParticipationJpaEntity {
            return ContestParticipationJpaEntity(
                id = if (participation.id.value == 0L) null else participation.id.value,
                member = member,
                contest = contestJpaEntity,
                feed = feed
            )
        }
    }
}
