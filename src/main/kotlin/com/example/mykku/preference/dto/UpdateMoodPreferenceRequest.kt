package com.example.mykku.preference.dto

import com.example.mykku.preference.domain.MoodType

data class UpdateMoodPreferenceRequest(
    val moodTypes: List<MoodType>
)
