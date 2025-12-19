package com.example.mykku.event.application.port.out

import com.example.mykku.event.domain.Event
import com.example.mykku.event.domain.EventImage
import com.example.mykku.event.dto.EventImageRequest

interface EventRepositoryPort {
    fun save(event: Event): Event
    fun saveImages(images: List<EventImage>): List<EventImage>
    fun createEvent(
        title: String,
        description: String?,
        startedAt: java.time.LocalDateTime,
        expiredAt: java.time.LocalDateTime,
        imageRequests: List<EventImageRequest>
    ): Pair<Event, List<EventImage>>
}
