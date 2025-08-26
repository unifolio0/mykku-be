package com.example.mykku.feed.tool

import com.example.mykku.feed.domain.Event
import com.example.mykku.feed.domain.EventImage
import com.example.mykku.feed.domain.EventTag
import com.example.mykku.feed.dto.EventImageRequest
import com.example.mykku.feed.repository.EventImageRepository
import com.example.mykku.feed.repository.EventRepository
import com.example.mykku.feed.repository.EventTagRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Component
class EventWriter(
    private val eventRepository: EventRepository,
    private val eventImageRepository: EventImageRepository,
    private val eventTagRepository: EventTagRepository
) {

    @Transactional
    fun createEvent(
        title: String,
        isContest: Boolean,
        expiredAt: LocalDateTime,
        imageRequests: List<EventImageRequest>,
        tagTitles: List<String>
    ): Event {
        val event = Event(
            title = title,
            isContest = isContest,
            expiredAt = expiredAt
        )

        val savedEvent = eventRepository.save(event)

        // Save images separately
        imageRequests.forEach { imageRequest ->
            val eventImage = EventImage(
                url = imageRequest.url,
                orderIndex = imageRequest.orderIndex,
                event = savedEvent
            )
            eventImageRepository.save(eventImage)
        }

        // Process and save tags separately
        val normalizedDistinctTags = tagTitles.asSequence()
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .distinct()
            .toList()

        normalizedDistinctTags.forEach { tagTitle ->
            val eventTag = EventTag(
                title = tagTitle,
                event = savedEvent
            )
            eventTagRepository.save(eventTag)
        }

        return savedEvent
    }
}