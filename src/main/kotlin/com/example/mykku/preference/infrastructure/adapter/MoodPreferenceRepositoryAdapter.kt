package com.example.mykku.preference.infrastructure.adapter

import com.example.mykku.member.domain.Member
import com.example.mykku.preference.application.port.out.MoodPreferenceRepositoryPort
import com.example.mykku.preference.domain.MemberMoodPreference
import com.example.mykku.preference.domain.MoodType
import com.example.mykku.preference.repository.MemberMoodPreferenceRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class MoodPreferenceRepositoryAdapter(
    private val memberMoodPreferenceRepository: MemberMoodPreferenceRepository
) : MoodPreferenceRepositoryPort {
    @Transactional
    override fun replacePreferences(member: Member, moodTypes: List<MoodType>) {
        memberMoodPreferenceRepository.deleteByMember(member)

        val preferences = moodTypes.map { moodType ->
            MemberMoodPreference(
                member = member,
                moodType = moodType
            )
        }

        memberMoodPreferenceRepository.saveAll(preferences)
    }
}
