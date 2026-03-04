package com.example.mykku.preference.application.port.output

import com.example.mykku.preference.domain.entity.MemberMoodPreference
import com.example.mykku.preference.domain.vo.MoodType

interface MoodPreferenceRepository {
    fun saveAll(preferences: List<MemberMoodPreference>): List<MemberMoodPreference>
    fun findByMemberId(memberId: Long): List<MemberMoodPreference>
    fun deleteByMemberId(memberId: Long)
    fun existsByMemberIdAndMoodType(memberId: Long, moodType: MoodType): Boolean
}
