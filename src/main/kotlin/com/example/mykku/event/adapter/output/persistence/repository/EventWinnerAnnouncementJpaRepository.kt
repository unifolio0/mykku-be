package com.example.mykku.event.adapter.output.persistence.repository

import com.example.mykku.event.adapter.output.persistence.entity.EventJpaEntity
import com.example.mykku.event.adapter.output.persistence.entity.EventWinnerAnnouncementJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EventWinnerAnnouncementJpaRepository : JpaRepository<EventWinnerAnnouncementJpaEntity, Long> {
    fun findByEvent(event: EventJpaEntity): EventWinnerAnnouncementJpaEntity?
}
