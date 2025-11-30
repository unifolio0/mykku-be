package com.example.mykku.event.tool

import com.example.mykku.event.domain.Event
import com.example.mykku.event.domain.EventImage
import com.example.mykku.event.dto.EventImageRequest
import com.example.mykku.event.exception.EventException
import com.example.mykku.event.repository.EventImageRepository
import com.example.mykku.event.repository.EventRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Component
class EventWriter(
    private val eventRepository: EventRepository,
    private val eventImageRepository: EventImageRepository
) {

    @Transactional
    fun createEvent(
        title: String,
        description: String?,
        expiredAt: LocalDateTime,
        imageRequests: List<EventImageRequest>
    ): Pair<Event, List<EventImage>> {

        if (imageRequests.size > Event.IMAGE_MAX_COUNT) {
            throw EventException.eventImageLimitExceeded()
        }

        val event = Event(
            title = title,
            description = description,
            expiredAt = expiredAt
        )

        val savedEvent = eventRepository.save(event)

        val eventImages = imageRequests.map { imageRequest ->
            EventImage(
                url = imageRequest.url,
                orderIndex = imageRequest.orderIndex,
                event = savedEvent
            )
        }
        val savedEventImages = eventImageRepository.saveAll(eventImages)

        return Pair(savedEvent, savedEventImages)
    }
}
