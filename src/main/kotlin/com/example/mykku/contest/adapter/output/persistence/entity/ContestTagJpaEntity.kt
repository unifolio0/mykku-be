package com.example.mykku.contest.adapter.output.persistence.entity

import com.example.mykku.common.adapter.persistence.BaseJpaEntity
import com.example.mykku.contest.domain.entity.ContestTag
import com.example.mykku.contest.domain.vo.ContestId
import com.example.mykku.contest.domain.vo.ContestTagId
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
@Table(name = "contest_tag")
class ContestTagJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "title")
    var title: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contest_id")
    val contest: ContestJpaEntity
) : BaseJpaEntity() {

    fun toDomain(): ContestTag {
        return ContestTag.reconstitute(
            id = ContestTagId.of(id!!),
            title = title,
            contestId = ContestId.of(contest.id!!),
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    companion object {
        fun fromDomain(contestTag: ContestTag, contestJpaEntity: ContestJpaEntity): ContestTagJpaEntity {
            return ContestTagJpaEntity(
                id = if (contestTag.id.value == 0L) null else contestTag.id.value,
                title = contestTag.title,
                contest = contestJpaEntity
            )
        }
    }
}
