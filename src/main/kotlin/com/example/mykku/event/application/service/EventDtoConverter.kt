package com.example.mykku.event.application.service

import com.example.mykku.event.domain.Event
import com.example.mykku.event.domain.EventImage
import com.example.mykku.event.dto.EventDetailResponse
import com.example.mykku.event.dto.EventImageResponse
import com.example.mykku.event.dto.EventListResponse
import org.springframework.stereotype.Component

@Component
class EventDtoConverter {

    fun toEventListResponse(
        event: Event,
        images: List<EventImage>,
        isSaved: Boolean
    ): EventListResponse {
        val sortedImages = sortImagesByOrder(images)
        return EventListResponse(
            id = event.id!!,
            title = event.title,
            startedAt = event.startedAt,
            expiredAt = event.expiredAt,
            status = event.status,
            thumbnailUrl = sortedImages.firstOrNull()?.url,
            isSaved = isSaved
        )
    }

    fun toEventDetailResponse(
        event: Event,
        images: List<EventImage>,
        isSaved: Boolean
    ): EventDetailResponse {
        val sortedImages = sortImagesByOrder(images)
        return EventDetailResponse(
            id = event.id!!,
            title = event.title,
            description = event.description,
            startedAt = event.startedAt,
            expiredAt = event.expiredAt,
            status = event.status,
            images = sortedImages.map { EventImageResponse(url = it.url, orderIndex = it.orderIndex) },
            isSaved = isSaved,
            createdAt = event.createdAt
        )
    }

    private fun sortImagesByOrder(images: List<EventImage>): List<EventImage> {
        return images.sortedBy { it.orderIndex }
    }
}
