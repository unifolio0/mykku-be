package com.example.mykku.preference.domain.model

import com.example.mykku.member.domain.model.MemberId
import com.example.mykku.preference.domain.GenreType
import java.time.Instant

class GenrePreferenceDomain private constructor(
    val id: GenrePreferenceId?,
    val memberId: MemberId,
    val genreType: GenreType,
    val createdAt: Instant
) {
    companion object {
        fun create(
            memberId: MemberId,
            genreType: GenreType
        ): GenrePreferenceDomain {
            return GenrePreferenceDomain(
                id = null,
                memberId = memberId,
                genreType = genreType,
                createdAt = Instant.now()
            )
        }

        fun reconstitute(
            id: GenrePreferenceId,
            memberId: MemberId,
            genreType: GenreType,
            createdAt: Instant
        ): GenrePreferenceDomain {
            return GenrePreferenceDomain(
                id = id,
                memberId = memberId,
                genreType = genreType,
                createdAt = createdAt
            )
        }
    }
}
