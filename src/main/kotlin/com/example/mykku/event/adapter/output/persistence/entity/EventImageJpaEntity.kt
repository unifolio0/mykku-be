package com.example.mykku.event.adapter.output.persistence.entity

import com.example.mykku.common.adapter.persistence.BaseJpaEntity
import com.example.mykku.event.domain.entity.EventImage
import com.example.mykku.event.domain.vo.EventId
import com.example.mykku.event.domain.vo.EventImageId
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "event_image")
class EventImageJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "url")
    var url: String,

    @Column(name = "order_index")
    var orderIndex: Int,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    val event: EventJpaEntity
) : BaseJpaEntity() {

    fun toDomain(): EventImage {
        return EventImage.reconstitute(
            id = EventImageId.of(id!!),
            url = url,
            orderIndex = orderIndex,
            eventId = EventId.of(event.id!!),
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    companion object {
        fun fromDomain(eventImage: EventImage, eventJpaEntity: EventJpaEntity): EventImageJpaEntity {
            return EventImageJpaEntity(
                id = if (eventImage.id.value == 0L) null else eventImage.id.value,
                url = eventImage.url,
                orderIndex = eventImage.orderIndex,
                event = eventJpaEntity
            )
        }
    }
}
