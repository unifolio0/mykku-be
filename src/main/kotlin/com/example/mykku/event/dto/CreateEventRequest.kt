package com.example.mykku.event.dto

import java.time.LocalDateTime

data class CreateEventRequest(
    val title: String,
    val expiredAt: LocalDateTime,
    val images: List<EventImageRequest> = emptyList()
)
