package com.example.mykku.event.adapter.output.persistence.entity

import com.example.mykku.common.domain.BaseEntity
import com.example.mykku.event.domain.entity.EventParticipation
import com.example.mykku.event.domain.vo.EventId
import com.example.mykku.event.domain.vo.EventParticipationId
import com.example.mykku.member.adapter.output.persistence.entity.MemberJpaEntity
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "event_participation")
class EventParticipationJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val member: MemberJpaEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    val event: EventJpaEntity
) : BaseEntity() {

    fun toDomain(): EventParticipation {
        return EventParticipation.reconstitute(
            id = EventParticipationId.of(id!!),
            eventId = EventId.of(event.id!!),
            memberId = member.id.hashCode().toLong(),
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    companion object {
        fun fromDomain(
            participation: EventParticipation,
            eventJpaEntity: EventJpaEntity,
            member: MemberJpaEntity
        ): EventParticipationJpaEntity {
            return EventParticipationJpaEntity(
                id = if (participation.id.value == 0L) null else participation.id.value,
                member = member,
                event = eventJpaEntity
            )
        }
    }
}
