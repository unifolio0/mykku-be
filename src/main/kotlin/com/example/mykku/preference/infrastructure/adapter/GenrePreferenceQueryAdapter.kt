package com.example.mykku.preference.infrastructure.adapter

import com.example.mykku.member.domain.Member
import com.example.mykku.preference.application.port.out.GenrePreferenceQueryPort
import com.example.mykku.preference.domain.MemberGenrePreference
import com.example.mykku.preference.repository.MemberGenrePreferenceRepository
import org.springframework.stereotype.Component

@Component
class GenrePreferenceQueryAdapter(
    private val memberGenrePreferenceRepository: MemberGenrePreferenceRepository
) : GenrePreferenceQueryPort {
    override fun findByMember(member: Member): List<MemberGenrePreference> {
        return memberGenrePreferenceRepository.findByMember(member)
    }
}
