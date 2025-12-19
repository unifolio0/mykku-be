package com.example.mykku.contest.domain.model

import com.example.mykku.contest.domain.ContestStatusType
import java.time.Instant
import java.time.LocalDateTime

class ContestDomain private constructor(
    val id: ContestId?,
    val title: String,
    val description: String?,
    val startedAt: LocalDateTime,
    val expiredAt: LocalDateTime,
    private var _scrapCount: Int,
    val status: ContestStatusType,
    val createdAt: Instant,
    private var _updatedAt: Instant
) {
    val scrapCount: Int get() = _scrapCount
    val updatedAt: Instant get() = _updatedAt

    companion object {
        const val IMAGE_MAX_COUNT = 10
        const val TAG_MAX_COUNT = 7

        fun create(
            title: String,
            description: String?,
            startedAt: LocalDateTime,
            expiredAt: LocalDateTime
        ): ContestDomain {
            val now = Instant.now()
            return ContestDomain(
                id = null,
                title = title,
                description = description,
                startedAt = startedAt,
                expiredAt = expiredAt,
                _scrapCount = 0,
                status = ContestStatusType.ACTIVE,
                createdAt = now,
                _updatedAt = now
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
            createdAt: Instant,
            updatedAt: Instant
        ): ContestDomain {
            return ContestDomain(
                id = id,
                title = title,
                description = description,
                startedAt = startedAt,
                expiredAt = expiredAt,
                _scrapCount = scrapCount,
                status = status,
                createdAt = createdAt,
                _updatedAt = updatedAt
            )
        }
    }

    fun incrementScrapCount() {
        _scrapCount++
        _updatedAt = Instant.now()
    }

    fun decrementScrapCount() {
        if (_scrapCount > 0) {
            _scrapCount--
            _updatedAt = Instant.now()
        }
    }

    fun isExpired(): Boolean {
        return LocalDateTime.now().isAfter(expiredAt)
    }

    fun isActive(): Boolean {
        val now = LocalDateTime.now()
        return now.isAfter(startedAt) && now.isBefore(expiredAt)
    }
}
