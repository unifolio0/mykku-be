package com.example.mykku.preference.domain

import com.example.mykku.common.domain.BaseEntity
import com.example.mykku.member.domain.Member
import jakarta.persistence.*

@Entity
@Table(name = "member_mood_preference")
class MemberMoodPreference(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    val member: Member,

    @Enumerated(EnumType.STRING)
    @Column(name = "mood_type", nullable = false, length = 50)
    val moodType: MoodType
) : BaseEntity()
