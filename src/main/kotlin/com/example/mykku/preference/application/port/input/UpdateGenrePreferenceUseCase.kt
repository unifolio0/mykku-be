package com.example.mykku.preference.application.port.input

import com.example.mykku.preference.application.dto.UpdateGenrePreferenceCommand

interface UpdateGenrePreferenceUseCase {
    fun updateGenrePreferences(command: UpdateGenrePreferenceCommand)
}
