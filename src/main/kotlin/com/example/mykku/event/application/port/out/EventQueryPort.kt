package com.example.mykku.event.application.port.out

import com.example.mykku.event.domain.model.EventId
import java.time.LocalDateTime

data class EventSummary(
    val id: Long,
    val title: String,
    val startedAt: LocalDateTime,
    val expiredAt: LocalDateTime
)

interface EventQueryPort {
    fun findSummaryById(id: EventId): EventSummary?
    fun existsById(id: EventId): Boolean
}
