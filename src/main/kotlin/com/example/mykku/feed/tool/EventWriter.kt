package com.example.mykku.feed.tool

import com.example.mykku.feed.exception.FeedException
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
    ): Triple<Event, List<EventImage>, List<EventTag>> {
        
        if (imageRequests.size > Event.IMAGE_MAX_COUNT) {
            throw FeedException.eventImageLimitExceeded()
        }

        val normalizedDistinctTags = tagTitles.asSequence()
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .distinct()
            .toList()

        if (normalizedDistinctTags.size > Event.TAG_MAX_COUNT) {
            throw FeedException.eventTagLimitExceeded()
        }

        val event = Event(
            title = title,
            isContest = isContest,
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

        val eventTags = normalizedDistinctTags.map { tagTitle ->
            EventTag(
                title = tagTitle,
                event = savedEvent
            )
        }
        val savedEventTags = eventTagRepository.saveAll(eventTags)

        return Triple(savedEvent, savedEventImages, savedEventTags)
    }
}