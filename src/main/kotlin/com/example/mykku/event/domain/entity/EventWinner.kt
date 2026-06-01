package com.example.mykku.event.domain.entity

import com.example.mykku.event.domain.vo.EventId
import com.example.mykku.event.domain.vo.EventParticipationId
import com.example.mykku.event.domain.vo.EventWinnerId
import java.time.LocalDateTime

class EventWinner private constructor(
    val id: EventWinnerId,
    val eventId: EventId,
    val participationId: EventParticipationId,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun create(
            eventId: EventId,
            participationId: EventParticipationId
        ): EventWinner {
            val now = LocalDateTime.now()
            return EventWinner(
                id = EventWinnerId(0),
                eventId = eventId,
                participationId = participationId,
                createdAt = now,
                updatedAt = now
            )
        }

        fun reconstitute(
            id: EventWinnerId,
            eventId: EventId,
            participationId: EventParticipationId,
            createdAt: LocalDateTime,
            updatedAt: LocalDateTime
        ): EventWinner {
            return EventWinner(
                id = id,
                eventId = eventId,
                participationId = participationId,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }
    }
}
