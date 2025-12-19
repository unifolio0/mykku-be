package com.example.mykku.event.infrastructure.adapter

import com.example.mykku.event.application.port.out.EventQueryPort
import com.example.mykku.event.application.port.out.EventSummary
import com.example.mykku.event.domain.model.EventId
import com.example.mykku.event.repository.EventRepository
import org.springframework.stereotype.Component

@Component
class EventQueryAdapter(
    private val eventRepository: EventRepository
) : EventQueryPort {

    override fun findSummaryById(id: EventId): EventSummary? {
        return eventRepository.findById(id.value)
            .map { event ->
                EventSummary(
                    id = event.id!!,
                    title = event.title,
                    startedAt = event.startedAt,
                    expiredAt = event.expiredAt
                )
            }
            .orElse(null)
    }

    override fun existsById(id: EventId): Boolean {
        return eventRepository.existsById(id.value)
    }
}
