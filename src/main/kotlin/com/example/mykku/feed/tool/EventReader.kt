package com.example.mykku.feed.tool

import com.example.mykku.feed.dto.EventPreviewResponse
import com.example.mykku.feed.repository.EventImageRepository
import com.example.mykku.feed.repository.EventRepository
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class EventReader(
    private val eventRepository: EventRepository,
    private val eventImageRepository: EventImageRepository,
) {
    fun getProcessingEventPreviews(): List<EventPreviewResponse> {
        val events = eventRepository.getByEventPreviews(LocalDateTime.now()).take(5)
        val eventImages = eventImageRepository.findByEventIn(events)
        val imagesByEvent = eventImages.groupBy { it.event }
        
        return events.map { event -> 
            EventPreviewResponse(event, imagesByEvent[event] ?: emptyList())
        }
    }
}
