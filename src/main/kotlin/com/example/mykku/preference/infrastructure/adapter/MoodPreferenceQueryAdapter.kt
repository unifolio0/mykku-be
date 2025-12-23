package com.example.mykku.preference.infrastructure.adapter

import com.example.mykku.member.domain.Member
import com.example.mykku.preference.application.port.out.MoodPreferenceQueryPort
import com.example.mykku.preference.domain.MemberMoodPreference
import com.example.mykku.preference.repository.MemberMoodPreferenceRepository
import org.springframework.stereotype.Component

@Component
class MoodPreferenceQueryAdapter(
    private val memberMoodPreferenceRepository: MemberMoodPreferenceRepository
) : MoodPreferenceQueryPort {
    override fun findByMember(member: Member): List<MemberMoodPreference> {
        return memberMoodPreferenceRepository.findByMember(member)
    }
}
