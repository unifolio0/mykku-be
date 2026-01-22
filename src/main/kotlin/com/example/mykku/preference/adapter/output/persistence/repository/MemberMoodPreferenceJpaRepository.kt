package com.example.mykku.preference.adapter.output.persistence.repository

import com.example.mykku.member.adapter.output.persistence.entity.MemberJpaEntity
import com.example.mykku.preference.adapter.output.persistence.entity.MemberMoodPreferenceJpaEntity
import com.example.mykku.preference.domain.vo.MoodType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MemberMoodPreferenceJpaRepository : JpaRepository<MemberMoodPreferenceJpaEntity, Long> {
    fun findByMember(member: MemberJpaEntity): List<MemberMoodPreferenceJpaEntity>
    fun findByMemberId(memberId: String): List<MemberMoodPreferenceJpaEntity>
    fun deleteByMember(member: MemberJpaEntity)
    fun deleteByMemberId(memberId: String)
    fun existsByMemberAndMoodType(member: MemberJpaEntity, moodType: MoodType): Boolean
    fun existsByMemberIdAndMoodType(memberId: String, moodType: MoodType): Boolean
}
