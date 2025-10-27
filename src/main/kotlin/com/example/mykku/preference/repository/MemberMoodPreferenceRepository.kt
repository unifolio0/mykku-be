package com.example.mykku.preference.repository

import com.example.mykku.member.domain.Member
import com.example.mykku.preference.domain.MemberMoodPreference
import com.example.mykku.preference.domain.MoodType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MemberMoodPreferenceRepository : JpaRepository<MemberMoodPreference, Long> {
    fun findByMember(member: Member): List<MemberMoodPreference>
    fun deleteByMember(member: Member)
    fun existsByMemberAndMoodType(member: Member, moodType: MoodType): Boolean
}
