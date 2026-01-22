package com.example.mykku.preference.application.dto

import com.example.mykku.preference.domain.vo.GenreType

data class UpdateGenrePreferenceCommand(
    val memberId: String,
    val genreTypes: List<GenreType>
)

data class GenrePreferenceResult(
    val genreTypes: List<GenreType>
)
