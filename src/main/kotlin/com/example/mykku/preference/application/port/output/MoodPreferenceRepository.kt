package com.example.mykku.preference.application.port.output

import com.example.mykku.preference.domain.entity.MemberMoodPreference
import com.example.mykku.preference.domain.vo.MoodType

interface MoodPreferenceRepository {
    fun saveAll(preferences: List<MemberMoodPreference>): List<MemberMoodPreference>
    fun findByMemberId(memberId: String): List<MemberMoodPreference>
    fun deleteByMemberId(memberId: String)
    fun existsByMemberIdAndMoodType(memberId: String, moodType: MoodType): Boolean
}
