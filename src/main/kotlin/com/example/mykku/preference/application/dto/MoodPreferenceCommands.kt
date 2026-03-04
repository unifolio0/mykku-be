package com.example.mykku.preference.application.dto

import com.example.mykku.preference.domain.vo.MoodType

data class UpdateMoodPreferenceCommand(
    val memberId: Long,
    val moodTypes: List<MoodType>
)

data class MoodPreferenceResult(
    val moodTypes: List<MoodType>
)
