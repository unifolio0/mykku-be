package com.example.mykku.preference.dto

import com.example.mykku.preference.domain.GenreType

data class UpdateGenrePreferenceRequest(
    val genreTypes: List<GenreType>
)
