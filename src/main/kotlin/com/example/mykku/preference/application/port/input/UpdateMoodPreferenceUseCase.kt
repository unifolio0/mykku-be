package com.example.mykku.preference.application.port.input

import com.example.mykku.preference.application.dto.UpdateMoodPreferenceCommand

interface UpdateMoodPreferenceUseCase {
    fun updateMoodPreferences(command: UpdateMoodPreferenceCommand)
}
