package com.example.mykku.preference.application.port.input

import com.example.mykku.preference.application.dto.GenrePreferenceResult

interface GetGenrePreferenceUseCase {
    fun getGenrePreferences(memberId: String): GenrePreferenceResult
}
