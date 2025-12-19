package com.example.mykku.scrap.domain.model

import com.example.mykku.event.domain.model.EventId
import com.example.mykku.member.domain.model.MemberId
import java.time.Instant

class SaveEventDomain private constructor(
    val id: SaveEventId?,
    val memberId: MemberId,
    val eventId: EventId,
    val createdAt: Instant
) {
    companion object {
        fun create(
            memberId: MemberId,
            eventId: EventId
        ): SaveEventDomain {
            return SaveEventDomain(
                id = null,
                memberId = memberId,
                eventId = eventId,
                createdAt = Instant.now()
            )
        }

        fun reconstitute(
            id: SaveEventId,
            memberId: MemberId,
            eventId: EventId,
            createdAt: Instant
        ): SaveEventDomain {
            return SaveEventDomain(
                id = id,
                memberId = memberId,
                eventId = eventId,
                createdAt = createdAt
            )
        }
    }
}
