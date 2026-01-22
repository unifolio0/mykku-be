package com.example.mykku.preference.application.port.output

import com.example.mykku.preference.domain.entity.MemberGenrePreference
import com.example.mykku.preference.domain.vo.GenreType

interface GenrePreferenceRepository {
    fun saveAll(preferences: List<MemberGenrePreference>): List<MemberGenrePreference>
    fun findByMemberId(memberId: String): List<MemberGenrePreference>
    fun deleteByMemberId(memberId: String)
    fun existsByMemberIdAndGenreType(memberId: String, genreType: GenreType): Boolean
}
