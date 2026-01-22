package com.example.mykku.event.domain.entity

import com.example.mykku.event.domain.vo.EventId
import com.example.mykku.event.domain.vo.EventImageId
import java.time.LocalDateTime

class EventImage private constructor(
    val id: EventImageId,
    val url: String,
    val orderIndex: Int,
    val eventId: EventId,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun create(
            url: String,
            orderIndex: Int,
            eventId: EventId
        ): EventImage {
            val now = LocalDateTime.now()
            return EventImage(
                id = EventImageId(0),
                url = url,
                orderIndex = orderIndex,
                eventId = eventId,
                createdAt = now,
                updatedAt = now
            )
        }

        fun reconstitute(
            id: EventImageId,
            url: String,
            orderIndex: Int,
            eventId: EventId,
            createdAt: LocalDateTime,
            updatedAt: LocalDateTime
        ): EventImage {
            return EventImage(
                id = id,
                url = url,
                orderIndex = orderIndex,
                eventId = eventId,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }
    }
}
