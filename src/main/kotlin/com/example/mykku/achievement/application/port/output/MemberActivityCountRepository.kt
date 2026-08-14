package com.example.mykku.achievement.application.port.output

import com.example.mykku.achievement.domain.vo.ActivityType

interface MemberActivityCountRepository {
    fun incrementAndGet(memberId: Long, activityType: ActivityType): Long
}
