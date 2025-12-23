package com.example.mykku.preference.application.port.out

import com.example.mykku.member.domain.Member
import com.example.mykku.preference.domain.MoodType

interface MoodPreferenceRepositoryPort {
    fun replacePreferences(member: Member, moodTypes: List<MoodType>)
}
