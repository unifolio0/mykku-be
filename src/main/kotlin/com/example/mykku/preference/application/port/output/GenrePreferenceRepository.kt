package com.example.mykku.preference.application.port.output

import com.example.mykku.preference.domain.entity.MemberGenrePreference
import com.example.mykku.preference.domain.vo.GenreType

interface GenrePreferenceRepository {
    fun saveAll(preferences: List<MemberGenrePreference>): List<MemberGenrePreference>
    fun findByMemberId(memberId: Long): List<MemberGenrePreference>
    fun deleteByMemberId(memberId: Long)
    fun existsByMemberIdAndGenreType(memberId: Long, genreType: GenreType): Boolean
}
