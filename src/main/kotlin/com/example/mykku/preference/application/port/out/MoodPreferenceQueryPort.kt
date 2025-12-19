package com.example.mykku.preference.application.port.out

import com.example.mykku.member.domain.Member
import com.example.mykku.preference.domain.MemberMoodPreference

interface MoodPreferenceQueryPort {
    fun findByMember(member: Member): List<MemberMoodPreference>
}
