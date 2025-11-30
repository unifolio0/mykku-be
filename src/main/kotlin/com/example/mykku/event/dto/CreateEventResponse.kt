package com.example.mykku.event.dto

import java.time.LocalDateTime

data class CreateEventResponse(
    val id: Long,
    val title: String,
    val expiredAt: LocalDateTime,
    val images: List<EventImageResponse>,
    val createdAt: LocalDateTime
)
