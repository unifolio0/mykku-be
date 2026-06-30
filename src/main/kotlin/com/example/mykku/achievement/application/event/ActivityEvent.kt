package com.example.mykku.achievement.application.event

import com.example.mykku.achievement.domain.vo.ActivityType

data class ActivityEvent(
    val memberId: Long,
    val activityType: ActivityType
)
