package com.example.mykku.event.dto

import java.time.LocalDateTime

data class CreateEventRequest(
    val title: String,
    val description: String? = null,
    val expiredAt: LocalDateTime,
    val images: List<EventImageRequest> = emptyList()
)

data class EventImageRequest(
    val url: String,
    val orderIndex: Int
)
