package com.example.mykku.preference.adapter.output.persistence

import com.example.mykku.member.adapter.output.persistence.MemberJpaRepository
import com.example.mykku.preference.adapter.output.persistence.entity.MemberMoodPreferenceJpaEntity
import com.example.mykku.preference.adapter.output.persistence.repository.MemberMoodPreferenceJpaRepository
import com.example.mykku.preference.application.port.output.MoodPreferenceRepository
import com.example.mykku.preference.domain.entity.MemberMoodPreference
import com.example.mykku.preference.domain.vo.MoodType
import org.springframework.stereotype.Component

@Component
class MoodPreferenceRepositoryAdapter(
    private val jpaRepository: MemberMoodPreferenceJpaRepository,
    private val memberJpaRepository: MemberJpaRepository
) : MoodPreferenceRepository {

    override fun saveAll(preferences: List<MemberMoodPreference>): List<MemberMoodPreference> {
        if (preferences.isEmpty()) return emptyList()

        val memberId = preferences.first().memberId
        val member = memberJpaRepository.findById(memberId).orElseThrow {
            IllegalArgumentException("Member not found: $memberId")
        }

        val entities = preferences.map { preference ->
            MemberMoodPreferenceJpaEntity.fromDomain(preference, member)
        }

        return jpaRepository.saveAll(entities).map { it.toDomain() }
    }

    override fun findByMemberId(memberId: String): List<MemberMoodPreference> {
        return jpaRepository.findByMemberId(memberId).map { it.toDomain() }
    }

    override fun deleteByMemberId(memberId: String) {
        jpaRepository.deleteByMemberId(memberId)
    }

    override fun existsByMemberIdAndMoodType(memberId: String, moodType: MoodType): Boolean {
        return jpaRepository.existsByMemberIdAndMoodType(memberId, moodType)
    }
}
