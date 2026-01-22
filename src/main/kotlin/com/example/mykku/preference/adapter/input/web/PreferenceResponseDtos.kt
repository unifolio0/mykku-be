package com.example.mykku.preference.adapter.input.web

import com.example.mykku.preference.domain.vo.GenreType
import com.example.mykku.preference.domain.vo.GoodsType
import com.example.mykku.preference.domain.vo.MoodType

data class GenrePreferenceResponse(
    val genreTypes: List<GenreType>
)

data class GoodsPreferenceResponse(
    val goodsTypes: List<GoodsType>
)

data class MoodPreferenceResponse(
    val moodTypes: List<MoodType>
)
