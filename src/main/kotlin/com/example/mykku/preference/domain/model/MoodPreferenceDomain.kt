package com.example.mykku.preference.domain.model

import com.example.mykku.member.domain.model.MemberId
import com.example.mykku.preference.domain.MoodType
import java.time.Instant

class MoodPreferenceDomain private constructor(
    val id: MoodPreferenceId?,
    val memberId: MemberId,
    val moodType: MoodType,
    val createdAt: Instant
) {
    companion object {
        fun create(
            memberId: MemberId,
            moodType: MoodType
        ): MoodPreferenceDomain {
            return MoodPreferenceDomain(
                id = null,
                memberId = memberId,
                moodType = moodType,
                createdAt = Instant.now()
            )
        }

        fun reconstitute(
            id: MoodPreferenceId,
            memberId: MemberId,
            moodType: MoodType,
            createdAt: Instant
        ): MoodPreferenceDomain {
            return MoodPreferenceDomain(
                id = id,
                memberId = memberId,
                moodType = moodType,
                createdAt = createdAt
            )
        }
    }
}
