package com.example.mykku.preference.application.port.out

import com.example.mykku.member.domain.Member
import com.example.mykku.preference.domain.MemberGenrePreference

interface GenrePreferenceQueryPort {
    fun findByMember(member: Member): List<MemberGenrePreference>
}
