package com.example.mykku.feed.tool

import com.example.mykku.feed.domain.Event
import com.example.mykku.feed.domain.EventImage
import com.example.mykku.feed.domain.EventTag
import com.example.mykku.feed.dto.EventDetailResponse
import com.example.mykku.feed.dto.EventImageResponse
import com.example.mykku.feed.dto.EventListResponse
import org.springframework.stereotype.Component

@Component
class EventDtoConverter {

    fun toEventListResponse(
        event: Event,
        images: List<EventImage>,
        tags: List<EventTag>,
        isSaved: Boolean
    ): EventListResponse {
        val sortedImages = sortImagesByOrder(images)
        return EventListResponse(
            id = event.id!!,
            title = event.title,
            isContest = event.isContest,
            expiredAt = event.expiredAt,
            thumbnailUrl = sortedImages.firstOrNull()?.url,
            tags = tags.map { it.title },
            isSaved = isSaved
        )
    }

    fun toEventDetailResponse(
        event: Event,
        images: List<EventImage>,
        tags: List<EventTag>,
        isSaved: Boolean
    ): EventDetailResponse {
        val sortedImages = sortImagesByOrder(images)
        return EventDetailResponse(
            id = event.id!!,
            title = event.title,
            isContest = event.isContest,
            expiredAt = event.expiredAt,
            images = sortedImages.map { EventImageResponse(url = it.url, orderIndex = it.orderIndex) },
            tags = tags.map { it.title },
            isSaved = isSaved,
            createdAt = event.createdAt
        )
    }

    private fun sortImagesByOrder(images: List<EventImage>): List<EventImage> {
        return images.sortedBy { it.orderIndex }
    }
}
