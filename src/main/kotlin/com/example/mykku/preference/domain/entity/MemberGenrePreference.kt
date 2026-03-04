package com.example.mykku.preference.domain.entity

import com.example.mykku.preference.domain.vo.GenreType
import com.example.mykku.preference.domain.vo.PreferenceId
import java.time.LocalDateTime

class MemberGenrePreference private constructor(
    val id: PreferenceId,
    val memberId: Long,
    val genreType: GenreType,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun create(
            memberId: Long,
            genreType: GenreType
        ): MemberGenrePreference {
            val now = LocalDateTime.now()
            return MemberGenrePreference(
                id = PreferenceId(0),
                memberId = memberId,
                genreType = genreType,
                createdAt = now,
                updatedAt = now
            )
        }

        fun reconstitute(
            id: PreferenceId,
            memberId: Long,
            genreType: GenreType,
            createdAt: LocalDateTime,
            updatedAt: LocalDateTime
        ): MemberGenrePreference {
            return MemberGenrePreference(
                id = id,
                memberId = memberId,
                genreType = genreType,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }
    }
}
