package com.example.mykku.preference.infrastructure.adapter

import com.example.mykku.member.domain.Member
import com.example.mykku.preference.application.port.out.GenrePreferenceRepositoryPort
import com.example.mykku.preference.domain.GenreType
import com.example.mykku.preference.domain.MemberGenrePreference
import com.example.mykku.preference.repository.MemberGenrePreferenceRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class GenrePreferenceRepositoryAdapter(
    private val memberGenrePreferenceRepository: MemberGenrePreferenceRepository
) : GenrePreferenceRepositoryPort {
    @Transactional
    override fun replacePreferences(member: Member, genreTypes: List<GenreType>) {
        memberGenrePreferenceRepository.deleteByMember(member)

        val preferences = genreTypes.map { genreType ->
            MemberGenrePreference(
                member = member,
                genreType = genreType
            )
        }

        memberGenrePreferenceRepository.saveAll(preferences)
    }
}
