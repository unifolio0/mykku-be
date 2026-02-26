package com.example.mykku.event.application.usecase

import com.example.mykku.event.application.dto.EventPreviewResult
import com.example.mykku.event.application.port.input.GetEventPreviewsUseCase
import com.example.mykku.event.application.port.output.EventImageRepository
import com.example.mykku.event.application.port.output.EventRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class GetEventPreviewsUseCaseImpl(
    private val eventRepository: EventRepository,
    private val eventImageRepository: EventImageRepository
) : GetEventPreviewsUseCase {

    @Transactional(readOnly = true)
    override fun execute(): List<EventPreviewResult> {
        val events = eventRepository.findByExpiredAtAfter(LocalDateTime.now()).take(5)

        if (events.isEmpty()) {
            return emptyList()
        }

        val eventIds = events.map { it.id }
        val imagesByEventId = eventImageRepository.findByEventIds(eventIds)
            .groupBy { it.eventId.value }

        return events.map { event ->
            val images = imagesByEventId[event.id.value] ?: emptyList()
            EventPreviewResult(
                id = event.id.value,
                thumbnailUrl = event.thumbnailUrl,
                images = images.map { it.url }
            )
        }
    }
}
