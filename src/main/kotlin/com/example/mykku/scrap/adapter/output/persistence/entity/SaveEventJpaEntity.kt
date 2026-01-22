package com.example.mykku.scrap.adapter.output.persistence.entity

import com.example.mykku.common.domain.BaseEntity
import com.example.mykku.event.domain.Event
import com.example.mykku.member.domain.Member
import com.example.mykku.scrap.domain.entity.SaveEventEntity
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "save_event")
class SaveEventJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val member: Member,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    val event: Event
) : BaseEntity() {

    fun toDomain(): SaveEventEntity {
        return SaveEventEntity.reconstitute(
            id = this.id!!,
            memberId = this.member.id,
            eventId = this.event.id!!,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt
        )
    }

    companion object {
        fun fromDomain(
            domain: SaveEventEntity,
            member: Member,
            event: Event
        ): SaveEventJpaEntity {
            return SaveEventJpaEntity(
                id = domain.id?.value,
                member = member,
                event = event
            )
        }
    }
}
