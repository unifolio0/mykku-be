package com.example.mykku.contest.adapter.output.persistence.entity

import com.example.mykku.common.domain.BaseEntity
import com.example.mykku.contest.domain.entity.ContestImage
import com.example.mykku.contest.domain.vo.ContestId
import com.example.mykku.contest.domain.vo.ContestImageId
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
@Table(name = "contest_image")
class ContestImageJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "url")
    var url: String,

    @Column(name = "order_index")
    var orderIndex: Int,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contest_id")
    val contest: ContestJpaEntity
) : BaseEntity() {

    fun toDomain(): ContestImage {
        return ContestImage.reconstitute(
            id = ContestImageId.of(id!!),
            url = url,
            orderIndex = orderIndex,
            contestId = ContestId.of(contest.id!!),
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    companion object {
        fun fromDomain(contestImage: ContestImage, contestJpaEntity: ContestJpaEntity): ContestImageJpaEntity {
            return ContestImageJpaEntity(
                id = if (contestImage.id.value == 0L) null else contestImage.id.value,
                url = contestImage.url,
                orderIndex = contestImage.orderIndex,
                contest = contestJpaEntity
            )
        }
    }
}
