package com.example.mykku.event.domain.entity

import com.example.mykku.event.domain.vo.EventId
import com.example.mykku.event.domain.vo.EventStatusType
import java.time.LocalDateTime

class Event private constructor(
    val id: EventId,
    val title: String,
    val description: String?,
    val startedAt: LocalDateTime,
    val expiredAt: LocalDateTime,
    val thumbnailUrl: String,
    private var _status: EventStatusType,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    val status: EventStatusType
        get() = _status

    fun updateStatus(status: EventStatusType) {
        _status = status
    }

    companion object {
        const val IMAGE_MAX_COUNT = 10

        fun create(
            title: String,
            description: String?,
            startedAt: LocalDateTime,
            expiredAt: LocalDateTime,
            thumbnailUrl: String
        ): Event {
            val now = LocalDateTime.now()
            return Event(
                id = EventId(0),
                title = title,
                description = description,
                startedAt = startedAt,
                expiredAt = expiredAt,
                thumbnailUrl = thumbnailUrl,
                _status = EventStatusType.ACTIVE,
                createdAt = now,
                updatedAt = now
            )
        }

        fun reconstitute(
            id: EventId,
            title: String,
            description: String?,
            startedAt: LocalDateTime,
            expiredAt: LocalDateTime,
            thumbnailUrl: String,
            status: EventStatusType,
            createdAt: LocalDateTime,
            updatedAt: LocalDateTime
        ): Event {
            return Event(
                id = id,
                title = title,
                description = description,
                startedAt = startedAt,
                expiredAt = expiredAt,
                thumbnailUrl = thumbnailUrl,
                _status = status,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }
    }
}
