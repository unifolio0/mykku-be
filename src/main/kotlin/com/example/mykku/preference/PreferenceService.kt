package com.example.mykku.preference

import com.example.mykku.member.domain.Member
import com.example.mykku.preference.application.port.out.GenrePreferenceQueryPort
import com.example.mykku.preference.application.port.out.GenrePreferenceRepositoryPort
import com.example.mykku.preference.application.port.out.GoodsPreferenceQueryPort
import com.example.mykku.preference.application.port.out.GoodsPreferenceRepositoryPort
import com.example.mykku.preference.application.port.out.MoodPreferenceQueryPort
import com.example.mykku.preference.application.port.out.MoodPreferenceRepositoryPort
import com.example.mykku.preference.domain.GenreType
import com.example.mykku.preference.domain.GoodsType
import com.example.mykku.preference.domain.MoodType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PreferenceService(
    private val genrePreferenceQueryPort: GenrePreferenceQueryPort,
    private val genrePreferenceRepositoryPort: GenrePreferenceRepositoryPort,
    private val goodsPreferenceQueryPort: GoodsPreferenceQueryPort,
    private val goodsPreferenceRepositoryPort: GoodsPreferenceRepositoryPort,
    private val moodPreferenceQueryPort: MoodPreferenceQueryPort,
    private val moodPreferenceRepositoryPort: MoodPreferenceRepositoryPort
) {
    @Transactional
    fun updateGenrePreferences(member: Member, genreTypes: List<GenreType>) {
        genrePreferenceRepositoryPort.replacePreferences(member, genreTypes)
    }

    @Transactional
    fun updateGoodsPreferences(member: Member, goodsTypes: List<GoodsType>) {
        goodsPreferenceRepositoryPort.replacePreferences(member, goodsTypes)
    }

    @Transactional
    fun updateMoodPreferences(member: Member, moodTypes: List<MoodType>) {
        moodPreferenceRepositoryPort.replacePreferences(member, moodTypes)
    }

    @Transactional(readOnly = true)
    fun getGenrePreferences(member: Member): List<GenreType> {
        return genrePreferenceQueryPort.findByMember(member)
            .map { it.genreType }
    }

    @Transactional(readOnly = true)
    fun getGoodsPreferences(member: Member): List<GoodsType> {
        return goodsPreferenceQueryPort.findByMember(member)
            .map { it.goodsType }
    }

    @Transactional(readOnly = true)
    fun getMoodPreferences(member: Member): List<MoodType> {
        return moodPreferenceQueryPort.findByMember(member)
            .map { it.moodType }
    }
}
