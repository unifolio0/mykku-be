package com.example.mykku.event.adapter.input.web

import com.example.mykku.event.application.dto.CreateEventCommand
import com.example.mykku.event.application.dto.EventImageCommand
import java.time.LocalDateTime

data class CreateEventRequest(
    val title: String,
    val description: String? = null,
    val startedAt: LocalDateTime,
    val expiredAt: LocalDateTime,
    val images: List<EventImageRequest> = emptyList()
) {
    fun toCommand(): CreateEventCommand {
        return CreateEventCommand(
            title = title,
            description = description,
            startedAt = startedAt,
            expiredAt = expiredAt,
            images = images.map { EventImageCommand(url = it.url, orderIndex = it.orderIndex) }
        )
    }
}

data class EventImageRequest(
    val url: String,
    val orderIndex: Int
)
