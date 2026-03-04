package com.example.mykku.event.domain.entity

import com.example.mykku.event.domain.vo.EventId
import com.example.mykku.event.domain.vo.EventParticipationId
import java.time.LocalDateTime

class EventParticipation private constructor(
    val id: EventParticipationId,
    val eventId: EventId,
    val memberId: Long?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun create(
            eventId: EventId,
            memberId: Long
        ): EventParticipation {
            val now = LocalDateTime.now()
            return EventParticipation(
                id = EventParticipationId(0),
                eventId = eventId,
                memberId = memberId,
                createdAt = now,
                updatedAt = now
            )
        }

        fun reconstitute(
            id: EventParticipationId,
            eventId: EventId,
            memberId: Long?,
            createdAt: LocalDateTime,
            updatedAt: LocalDateTime
        ): EventParticipation {
            return EventParticipation(
                id = id,
                eventId = eventId,
                memberId = memberId,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }
    }
}
