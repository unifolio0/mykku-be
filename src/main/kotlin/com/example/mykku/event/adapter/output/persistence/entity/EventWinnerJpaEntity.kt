package com.example.mykku.event.adapter.output.persistence.entity

import com.example.mykku.common.adapter.persistence.BaseJpaEntity
import com.example.mykku.event.domain.entity.EventWinner
import com.example.mykku.event.domain.vo.EventId
import com.example.mykku.event.domain.vo.EventParticipationId
import com.example.mykku.event.domain.vo.EventWinnerId
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "event_winner")
class EventWinnerJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    val event: EventJpaEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participation_id")
    val participation: EventParticipationJpaEntity
) : BaseJpaEntity() {

    fun toDomain(): EventWinner {
        return EventWinner.reconstitute(
            id = EventWinnerId.of(id!!),
            eventId = EventId.of(event.id!!),
            participationId = EventParticipationId.of(participation.id!!),
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    companion object {
        fun fromDomain(
            winner: EventWinner,
            eventJpaEntity: EventJpaEntity,
            participationJpaEntity: EventParticipationJpaEntity
        ): EventWinnerJpaEntity {
            return EventWinnerJpaEntity(
                id = if (winner.id.value == 0L) null else winner.id.value,
                event = eventJpaEntity,
                participation = participationJpaEntity
            )
        }
    }
}
