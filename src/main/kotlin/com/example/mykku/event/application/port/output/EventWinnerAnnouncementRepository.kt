package com.example.mykku.event.application.port.output

import com.example.mykku.event.domain.entity.EventWinnerAnnouncement
import com.example.mykku.event.domain.vo.EventId

interface EventWinnerAnnouncementRepository {
    fun save(announcement: EventWinnerAnnouncement): EventWinnerAnnouncement
    fun findByEventId(eventId: EventId): EventWinnerAnnouncement?
}
