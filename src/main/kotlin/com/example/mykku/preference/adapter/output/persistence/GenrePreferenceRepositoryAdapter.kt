package com.example.mykku.preference.adapter.output.persistence

import com.example.mykku.member.domain.Member
import com.example.mykku.member.repository.MemberRepository
import com.example.mykku.preference.adapter.output.persistence.entity.MemberGenrePreferenceJpaEntity
import com.example.mykku.preference.adapter.output.persistence.repository.MemberGenrePreferenceJpaRepository
import com.example.mykku.preference.application.port.output.GenrePreferenceRepository
import com.example.mykku.preference.domain.entity.MemberGenrePreference
import com.example.mykku.preference.domain.vo.GenreType
import org.springframework.stereotype.Component

@Component
class GenrePreferenceRepositoryAdapter(
    private val jpaRepository: MemberGenrePreferenceJpaRepository,
    private val memberRepository: MemberRepository
) : GenrePreferenceRepository {

    override fun saveAll(preferences: List<MemberGenrePreference>): List<MemberGenrePreference> {
        if (preferences.isEmpty()) return emptyList()

        val memberId = preferences.first().memberId
        val member = memberRepository.findById(memberId).orElseThrow {
            IllegalArgumentException("Member not found: $memberId")
        }

        val entities = preferences.map { preference ->
            MemberGenrePreferenceJpaEntity.fromDomain(preference, member)
        }

        return jpaRepository.saveAll(entities).map { it.toDomain() }
    }

    override fun findByMemberId(memberId: String): List<MemberGenrePreference> {
        return jpaRepository.findByMemberId(memberId).map { it.toDomain() }
    }

    override fun deleteByMemberId(memberId: String) {
        jpaRepository.deleteByMemberId(memberId)
    }

    override fun existsByMemberIdAndGenreType(memberId: String, genreType: GenreType): Boolean {
        return jpaRepository.existsByMemberIdAndGenreType(memberId, genreType)
    }
}
