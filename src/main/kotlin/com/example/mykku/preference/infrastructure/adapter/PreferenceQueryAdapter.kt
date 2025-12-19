package com.example.mykku.preference.infrastructure.adapter

import com.example.mykku.member.domain.model.MemberId
import com.example.mykku.preference.application.port.out.PreferenceQueryPort
import com.example.mykku.preference.domain.GenreType
import com.example.mykku.preference.domain.GoodsType
import com.example.mykku.preference.domain.MoodType
import com.example.mykku.preference.repository.MemberGenrePreferenceRepository
import com.example.mykku.preference.repository.MemberGoodsPreferenceRepository
import com.example.mykku.preference.repository.MemberMoodPreferenceRepository
import org.springframework.stereotype.Component

@Component
class PreferenceQueryAdapter(
    private val genrePreferenceRepository: MemberGenrePreferenceRepository,
    private val goodsPreferenceRepository: MemberGoodsPreferenceRepository,
    private val moodPreferenceRepository: MemberMoodPreferenceRepository
) : PreferenceQueryPort {

    override fun getGenrePreferences(memberId: MemberId): List<GenreType> {
        return genrePreferenceRepository.findByMemberId(memberId.value)
            .map { it.genreType }
    }

    override fun getGoodsPreferences(memberId: MemberId): List<GoodsType> {
        return goodsPreferenceRepository.findByMemberId(memberId.value)
            .map { it.goodsType }
    }

    override fun getMoodPreferences(memberId: MemberId): List<MoodType> {
        return moodPreferenceRepository.findByMemberId(memberId.value)
            .map { it.moodType }
    }

    override fun hasGenrePreference(memberId: MemberId, genreType: GenreType): Boolean {
        return genrePreferenceRepository.existsByMemberIdAndGenreType(memberId.value, genreType)
    }

    override fun hasGoodsPreference(memberId: MemberId, goodsType: GoodsType): Boolean {
        return goodsPreferenceRepository.existsByMemberIdAndGoodsType(memberId.value, goodsType)
    }

    override fun hasMoodPreference(memberId: MemberId, moodType: MoodType): Boolean {
        return moodPreferenceRepository.existsByMemberIdAndMoodType(memberId.value, moodType)
    }
}
