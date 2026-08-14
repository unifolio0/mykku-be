package com.example.mykku.achievement.domain.entity

import com.example.mykku.achievement.domain.vo.ActivityCountId
import com.example.mykku.achievement.domain.vo.ActivityType
import java.time.LocalDateTime

class MemberActivityCount private constructor(
    val id: ActivityCountId,
    val memberId: Long,
    val activityType: ActivityType,
    val count: Long,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun create(memberId: Long, activityType: ActivityType): MemberActivityCount {
            val now = LocalDateTime.now()
            return MemberActivityCount(
                id = ActivityCountId(0),
                memberId = memberId,
                activityType = activityType,
                count = 1,
                createdAt = now,
                updatedAt = now
            )
        }

        fun reconstitute(
            id: ActivityCountId,
            memberId: Long,
            activityType: ActivityType,
            count: Long,
            createdAt: LocalDateTime,
            updatedAt: LocalDateTime
        ): MemberActivityCount {
            return MemberActivityCount(id, memberId, activityType, count, createdAt, updatedAt)
        }
    }
}
