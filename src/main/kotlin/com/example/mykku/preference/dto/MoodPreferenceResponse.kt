package com.example.mykku.preference.dto

import com.example.mykku.preference.domain.MoodType

data class MoodPreferenceResponse(
    val moodTypes: List<MoodType>
)
