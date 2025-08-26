package com.example.mykku.feed.tool

import com.example.mykku.exception.ErrorCode
import com.example.mykku.exception.MykkuException
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
        // 이미지 개수 제한 검증
        if (imageRequests.size > Event.IMAGE_MAX_COUNT) {
            throw MykkuException(ErrorCode.EVENT_IMAGE_LIMIT_EXCEEDED)
        }

        // 태그 유효성 검증 및 정규화
        val normalizedDistinctTags = tagTitles.asSequence()
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .distinct()
            .toList()

        // 태그 개수 제한 검증
        if (normalizedDistinctTags.size > Event.TAG_MAX_COUNT) {
            throw MykkuException(ErrorCode.EVENT_TAG_LIMIT_EXCEEDED)
        }

        val event = Event(
            title = title,
            isContest = isContest,
            expiredAt = expiredAt
        )

        val savedEvent = eventRepository.save(event)

        // Save images using saveAll for better performance
        val eventImages = imageRequests.map { imageRequest ->
            EventImage(
                url = imageRequest.url,
                orderIndex = imageRequest.orderIndex,
                event = savedEvent
            )
        }
        val savedEventImages = eventImageRepository.saveAll(eventImages)

        // Save tags using saveAll

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