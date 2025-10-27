package com.example.mykku.preference.tool

import com.example.mykku.member.domain.Member
import com.example.mykku.preference.domain.MemberGenrePreference
import com.example.mykku.preference.repository.MemberGenrePreferenceRepository
import org.springframework.stereotype.Component

@Component
class GenrePreferenceReader(
    private val memberGenrePreferenceRepository: MemberGenrePreferenceRepository
) {
    fun findByMember(member: Member): List<MemberGenrePreference> {
        return memberGenrePreferenceRepository.findByMember(member)
    }
}
