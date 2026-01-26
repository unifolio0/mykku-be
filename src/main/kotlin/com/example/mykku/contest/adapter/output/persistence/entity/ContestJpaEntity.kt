package com.example.mykku.contest.adapter.output.persistence.entity

import com.example.mykku.common.adapter.persistence.BaseJpaEntity
import com.example.mykku.contest.domain.entity.Contest
import com.example.mykku.contest.domain.vo.ContestId
import com.example.mykku.contest.domain.vo.ContestStatusType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "contest")
class ContestJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "title")
    var title: String,

    @Column(name = "description", columnDefinition = "TEXT")
    var description: String? = null,

    @Column(name = "started_at")
    var startedAt: LocalDateTime,

    @Column(name = "expired_at")
    var expiredAt: LocalDateTime,

    @Column(name = "scrap_count")
    var scrapCount: Int = 0,

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    var status: ContestStatusType = ContestStatusType.ACTIVE
) : BaseJpaEntity() {

    fun toDomain(): Contest {
        return Contest.reconstitute(
            id = ContestId.of(id!!),
            title = title,
            description = description,
            startedAt = startedAt,
            expiredAt = expiredAt,
            scrapCount = scrapCount,
            status = status,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    companion object {
        const val IMAGE_MAX_COUNT = 10
        const val TAG_MAX_COUNT = 7

        fun fromDomain(contest: Contest): ContestJpaEntity {
            return ContestJpaEntity(
                id = if (contest.id.value == 0L) null else contest.id.value,
                title = contest.title,
                description = contest.description,
                startedAt = contest.startedAt,
                expiredAt = contest.expiredAt,
                scrapCount = contest.scrapCount,
                status = contest.status
            )
        }
    }
}
