package com.example.mykku.preference.adapter.input.web

import com.example.mykku.preference.domain.vo.GenreType
import com.example.mykku.preference.domain.vo.GoodsType
import com.example.mykku.preference.domain.vo.MoodType

data class UpdateGenrePreferenceRequest(
    val genreTypes: List<GenreType>
)

data class UpdateGoodsPreferenceRequest(
    val goodsTypes: List<GoodsType>
)

data class UpdateMoodPreferenceRequest(
    val moodTypes: List<MoodType>
)
