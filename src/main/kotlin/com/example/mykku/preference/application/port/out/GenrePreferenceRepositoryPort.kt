package com.example.mykku.preference.application.port.out

import com.example.mykku.member.domain.Member
import com.example.mykku.preference.domain.GenreType

interface GenrePreferenceRepositoryPort {
    fun replacePreferences(member: Member, genreTypes: List<GenreType>)
}
