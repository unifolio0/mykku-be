package com.example.mykku.event.domain.model

import com.example.mykku.event.domain.EventStatusType
import java.time.Instant
import java.time.LocalDateTime

class EventDomain private constructor(
    val id: EventId?,
    val title: String,
    val description: String?,
    val startedAt: LocalDateTime,
    val expiredAt: LocalDateTime,
    private var _scrapCount: Int,
    val status: EventStatusType,
    val createdAt: Instant,
    private var _updatedAt: Instant
) {
    val scrapCount: Int get() = _scrapCount
    val updatedAt: Instant get() = _updatedAt

    companion object {
        const val IMAGE_MAX_COUNT = 10

        fun create(
            title: String,
            description: String?,
            startedAt: LocalDateTime,
            expiredAt: LocalDateTime
        ): EventDomain {
            val now = Instant.now()
            return EventDomain(
                id = null,
                title = title,
                description = description,
                startedAt = startedAt,
                expiredAt = expiredAt,
                _scrapCount = 0,
                status = EventStatusType.ACTIVE,
                createdAt = now,
                _updatedAt = now
            )
        }

        fun reconstitute(
            id: EventId,
            title: String,
            description: String?,
            startedAt: LocalDateTime,
            expiredAt: LocalDateTime,
            scrapCount: Int,
            status: EventStatusType,
            createdAt: Instant,
            updatedAt: Instant
        ): EventDomain {
            return EventDomain(
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
}
