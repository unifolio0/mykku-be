package com.example.mykku.feed.tool

import com.example.mykku.feed.dto.EventPreviewResponse
import com.example.mykku.feed.repository.EventRepository
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class EventReader(
    private val eventRepository: EventRepository,
) {
    fun getProcessingEventPreviews(): List<EventPreviewResponse> {
        return eventRepository.getByEventPreviews(LocalDateTime.now())
            .map { basicEvent -> EventPreviewResponse(basicEvent) }
            .take(5)
    }
}
