package com.example.mykku.preference

import com.example.mykku.member.domain.Member
import com.example.mykku.preference.domain.GenreType
import com.example.mykku.preference.domain.GoodsType
import com.example.mykku.preference.domain.MoodType
import com.example.mykku.preference.tool.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PreferenceService(
    private val genrePreferenceReader: GenrePreferenceReader,
    private val genrePreferenceWriter: GenrePreferenceWriter,
    private val goodsPreferenceReader: GoodsPreferenceReader,
    private val goodsPreferenceWriter: GoodsPreferenceWriter,
    private val moodPreferenceReader: MoodPreferenceReader,
    private val moodPreferenceWriter: MoodPreferenceWriter
) {
    @Transactional
    fun updateGenrePreferences(member: Member, genreTypes: List<GenreType>) {
        genrePreferenceWriter.replacePreferences(member, genreTypes)
    }

    @Transactional
    fun updateGoodsPreferences(member: Member, goodsTypes: List<GoodsType>) {
        goodsPreferenceWriter.replacePreferences(member, goodsTypes)
    }

    @Transactional
    fun updateMoodPreferences(member: Member, moodTypes: List<MoodType>) {
        moodPreferenceWriter.replacePreferences(member, moodTypes)
    }

    @Transactional(readOnly = true)
    fun getGenrePreferences(member: Member): List<GenreType> {
        return genrePreferenceReader.findByMember(member)
            .map { it.genreType }
    }

    @Transactional(readOnly = true)
    fun getGoodsPreferences(member: Member): List<GoodsType> {
        return goodsPreferenceReader.findByMember(member)
            .map { it.goodsType }
    }

    @Transactional(readOnly = true)
    fun getMoodPreferences(member: Member): List<MoodType> {
        return moodPreferenceReader.findByMember(member)
            .map { it.moodType }
    }
}
