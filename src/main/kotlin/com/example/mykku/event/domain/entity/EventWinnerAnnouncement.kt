package com.example.mykku.event.domain.entity

import com.example.mykku.event.domain.vo.EventId
import com.example.mykku.event.domain.vo.EventWinnerAnnouncementId
import java.time.LocalDate
import java.time.LocalDateTime

class EventWinnerAnnouncement private constructor(
    val id: EventWinnerAnnouncementId,
    val eventId: EventId,
    val title: String,
    val content: String,
    val announcedAt: LocalDate,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun create(
            eventId: EventId,
            title: String,
            content: String,
            announcedAt: LocalDate
        ): EventWinnerAnnouncement {
            val now = LocalDateTime.now()
            return EventWinnerAnnouncement(
                id = EventWinnerAnnouncementId(0L),
                eventId = eventId,
                title = title,
                content = content,
                announcedAt = announcedAt,
                createdAt = now,
                updatedAt = now
            )
        }

        fun reconstitute(
            id: EventWinnerAnnouncementId,
            eventId: EventId,
            title: String,
            content: String,
            announcedAt: LocalDate,
            createdAt: LocalDateTime,
            updatedAt: LocalDateTime
        ): EventWinnerAnnouncement {
            return EventWinnerAnnouncement(
                id = id,
                eventId = eventId,
                title = title,
                content = content,
                announcedAt = announcedAt,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }
    }

    fun update(
        title: String,
        content: String,
        announcedAt: LocalDate
    ): EventWinnerAnnouncement {
        return EventWinnerAnnouncement(
            id = this.id,
            eventId = this.eventId,
            title = title,
            content = content,
            announcedAt = announcedAt,
            createdAt = this.createdAt,
            updatedAt = LocalDateTime.now()
        )
    }
}
