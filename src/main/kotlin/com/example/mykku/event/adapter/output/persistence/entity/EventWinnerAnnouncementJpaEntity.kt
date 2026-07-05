package com.example.mykku.event.adapter.output.persistence.entity

import com.example.mykku.common.adapter.persistence.BaseJpaEntity
import com.example.mykku.event.domain.entity.EventWinnerAnnouncement
import com.example.mykku.event.domain.vo.EventId
import com.example.mykku.event.domain.vo.EventWinnerAnnouncementId
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDate

@Entity
@Table(name = "event_winner_announcement")
class EventWinnerAnnouncementJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    val event: EventJpaEntity,

    @Column(name = "title")
    var title: String,

    @Column(name = "content", columnDefinition = "TEXT")
    var content: String,

    @Column(name = "announced_at")
    var announcedAt: LocalDate
) : BaseJpaEntity() {

    fun toDomain(): EventWinnerAnnouncement {
        return EventWinnerAnnouncement.reconstitute(
            id = EventWinnerAnnouncementId.of(id!!),
            eventId = EventId.of(event.id!!),
            title = title,
            content = content,
            announcedAt = announcedAt,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    fun updateFromDomain(announcement: EventWinnerAnnouncement) {
        this.title = announcement.title
        this.content = announcement.content
        this.announcedAt = announcement.announcedAt
    }

    companion object {
        fun fromDomain(
            announcement: EventWinnerAnnouncement,
            eventJpaEntity: EventJpaEntity
        ): EventWinnerAnnouncementJpaEntity {
            return EventWinnerAnnouncementJpaEntity(
                id = if (announcement.id.value == 0L) null else announcement.id.value,
                event = eventJpaEntity,
                title = announcement.title,
                content = announcement.content,
                announcedAt = announcement.announcedAt
            )
        }
    }
}
