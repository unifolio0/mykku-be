package com.example.mykku.achievement.application.port.input

import com.example.mykku.achievement.domain.vo.ActivityType

interface AwardTitlesUseCase {
    fun handleActivity(memberId: Long, activityType: ActivityType)
}
