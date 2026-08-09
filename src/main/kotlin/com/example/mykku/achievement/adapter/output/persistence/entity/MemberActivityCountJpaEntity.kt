package com.example.mykku.achievement.adapter.output.persistence.entity

import com.example.mykku.achievement.domain.entity.MemberActivityCount
import com.example.mykku.achievement.domain.vo.ActivityCountId
import com.example.mykku.achievement.domain.vo.ActivityType
import com.example.mykku.common.adapter.persistence.BaseJpaEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "member_activity_count")
class MemberActivityCountJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "member_id", nullable = false)
    val memberId: Long,

    @Enumerated(EnumType.STRING)
    @Column(name = "activity_type", nullable = false, length = 50)
    val activityType: ActivityType,

    @Column(name = "count", nullable = false)
    val count: Long
) : BaseJpaEntity() {

    fun toDomain(): MemberActivityCount {
        return MemberActivityCount.reconstitute(
            id = ActivityCountId.of(id!!),
            memberId = memberId,
            activityType = activityType,
            count = count,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    companion object {
        fun fromDomain(activityCount: MemberActivityCount): MemberActivityCountJpaEntity {
            return MemberActivityCountJpaEntity(
                id = if (activityCount.id.value == 0L) null else activityCount.id.value,
                memberId = activityCount.memberId,
                activityType = activityCount.activityType,
                count = activityCount.count
            )
        }
    }
}
