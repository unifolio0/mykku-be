package com.example.mykku.preference.tool

import com.example.mykku.member.domain.Member
import com.example.mykku.preference.domain.MemberMoodPreference
import com.example.mykku.preference.repository.MemberMoodPreferenceRepository
import org.springframework.stereotype.Component

@Component
class MoodPreferenceReader(
    private val memberMoodPreferenceRepository: MemberMoodPreferenceRepository
) {
    fun findByMember(member: Member): List<MemberMoodPreference> {
        return memberMoodPreferenceRepository.findByMember(member)
    }
}
