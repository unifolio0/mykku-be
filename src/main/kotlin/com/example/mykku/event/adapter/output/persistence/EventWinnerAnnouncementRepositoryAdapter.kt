package com.example.mykku.event.adapter.output.persistence

import com.example.mykku.event.adapter.output.persistence.entity.EventWinnerAnnouncementJpaEntity
import com.example.mykku.event.adapter.output.persistence.repository.EventJpaRepository
import com.example.mykku.event.adapter.output.persistence.repository.EventWinnerAnnouncementJpaRepository
import com.example.mykku.event.application.port.output.EventWinnerAnnouncementRepository
import com.example.mykku.event.domain.entity.EventWinnerAnnouncement
import com.example.mykku.event.domain.vo.EventId
import com.example.mykku.event.exception.EventException
import org.springframework.stereotype.Repository

@Repository
class EventWinnerAnnouncementRepositoryAdapter(
    private val eventWinnerAnnouncementJpaRepository: EventWinnerAnnouncementJpaRepository,
    private val eventJpaRepository: EventJpaRepository
) : EventWinnerAnnouncementRepository {

    override fun save(announcement: EventWinnerAnnouncement): EventWinnerAnnouncement {
        val eventJpaEntity = eventJpaRepository.findById(announcement.eventId.value)
            .orElseThrow { EventException.eventNotFound() }

        val existing = eventWinnerAnnouncementJpaRepository.findByEvent(eventJpaEntity)
        val saved = if (existing != null) {
            existing.updateFromDomain(announcement)
            eventWinnerAnnouncementJpaRepository.save(existing)
        } else {
            val jpaEntity = EventWinnerAnnouncementJpaEntity.fromDomain(announcement, eventJpaEntity)
            eventWinnerAnnouncementJpaRepository.save(jpaEntity)
        }
        return saved.toDomain()
    }

    override fun findByEventId(eventId: EventId): EventWinnerAnnouncement? {
        val eventJpaEntity = eventJpaRepository.findById(eventId.value).orElse(null)
            ?: return null
        return eventWinnerAnnouncementJpaRepository.findByEvent(eventJpaEntity)?.toDomain()
    }
}
