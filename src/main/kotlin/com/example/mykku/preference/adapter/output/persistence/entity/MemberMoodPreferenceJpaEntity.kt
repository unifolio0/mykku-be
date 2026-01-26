package com.example.mykku.preference.adapter.output.persistence.entity

import com.example.mykku.common.adapter.persistence.BaseJpaEntity
import com.example.mykku.member.adapter.output.persistence.entity.MemberJpaEntity
import com.example.mykku.preference.domain.entity.MemberMoodPreference
import com.example.mykku.preference.domain.vo.MoodType
import com.example.mykku.preference.domain.vo.PreferenceId
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "member_mood_preference")
class MemberMoodPreferenceJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    val member: MemberJpaEntity,

    @Enumerated(EnumType.STRING)
    @Column(name = "mood_type", nullable = false, length = 50)
    val moodType: MoodType
) : BaseJpaEntity() {

    fun toDomain(): MemberMoodPreference {
        return MemberMoodPreference.reconstitute(
            id = PreferenceId.of(id),
            memberId = member.id,
            moodType = moodType,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    companion object {
        fun fromDomain(domain: MemberMoodPreference, member: MemberJpaEntity): MemberMoodPreferenceJpaEntity {
            return MemberMoodPreferenceJpaEntity(
                id = domain.id.value,
                member = member,
                moodType = domain.moodType
            )
        }
    }
}
