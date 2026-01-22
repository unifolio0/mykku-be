package com.example.mykku.preference.application.port.input

import com.example.mykku.preference.application.dto.MoodPreferenceResult

interface GetMoodPreferenceUseCase {
    fun getMoodPreferences(memberId: String): MoodPreferenceResult
}
