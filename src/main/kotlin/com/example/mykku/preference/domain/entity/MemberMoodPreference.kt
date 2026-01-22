package com.example.mykku.preference.domain.entity

import com.example.mykku.preference.domain.vo.MoodType
import com.example.mykku.preference.domain.vo.PreferenceId
import java.time.LocalDateTime

class MemberMoodPreference private constructor(
    val id: PreferenceId,
    val memberId: String,
    val moodType: MoodType,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun create(
            memberId: String,
            moodType: MoodType
        ): MemberMoodPreference {
            val now = LocalDateTime.now()
            return MemberMoodPreference(
                id = PreferenceId(0),
                memberId = memberId,
                moodType = moodType,
                createdAt = now,
                updatedAt = now
            )
        }

        fun reconstitute(
            id: PreferenceId,
            memberId: String,
            moodType: MoodType,
            createdAt: LocalDateTime,
            updatedAt: LocalDateTime
        ): MemberMoodPreference {
            return MemberMoodPreference(
                id = id,
                memberId = memberId,
                moodType = moodType,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }
    }
}
