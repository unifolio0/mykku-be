package com.example.mykku.preference.application.port.out

import com.example.mykku.member.domain.model.MemberId
import com.example.mykku.preference.domain.GenreType
import com.example.mykku.preference.domain.GoodsType
import com.example.mykku.preference.domain.MoodType

interface PreferenceQueryPort {
    fun getGenrePreferences(memberId: MemberId): List<GenreType>
    fun getGoodsPreferences(memberId: MemberId): List<GoodsType>
    fun getMoodPreferences(memberId: MemberId): List<MoodType>

    fun hasGenrePreference(memberId: MemberId, genreType: GenreType): Boolean
    fun hasGoodsPreference(memberId: MemberId, goodsType: GoodsType): Boolean
    fun hasMoodPreference(memberId: MemberId, moodType: MoodType): Boolean
}
