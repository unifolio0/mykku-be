package com.example.mykku.contest.domain.entity

import com.example.mykku.contest.domain.vo.ContestId
import com.example.mykku.contest.domain.vo.ContestStatusType
import java.time.LocalDateTime

class Contest private constructor(
    val id: ContestId,
    val title: String,
    val description: String?,
    val startedAt: LocalDateTime,
    val expiredAt: LocalDateTime,
    val scrapCount: Int,
    private var _status: ContestStatusType,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    val status: ContestStatusType
        get() = _status

    fun updateStatus(status: ContestStatusType) {
        _status = status
    }

    companion object {
        const val IMAGE_MAX_COUNT = 10
        const val TAG_MAX_COUNT = 7

        fun create(
            title: String,
            description: String?,
            startedAt: LocalDateTime,
            expiredAt: LocalDateTime
        ): Contest {
            val now = LocalDateTime.now()
            return Contest(
                id = ContestId(0),
                title = title,
                description = description,
                startedAt = startedAt,
                expiredAt = expiredAt,
                scrapCount = 0,
                _status = ContestStatusType.ACTIVE,
                createdAt = now,
                updatedAt = now
            )
        }

        fun reconstitute(
            id: ContestId,
            title: String,
            description: String?,
            startedAt: LocalDateTime,
            expiredAt: LocalDateTime,
            scrapCount: Int,
            status: ContestStatusType,
            createdAt: LocalDateTime,
            updatedAt: LocalDateTime
        ): Contest {
            return Contest(
                id = id,
                title = title,
                description = description,
                startedAt = startedAt,
                expiredAt = expiredAt,
                scrapCount = scrapCount,
                _status = status,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }
    }
}
