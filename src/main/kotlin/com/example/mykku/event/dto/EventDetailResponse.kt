package com.example.mykku.event.dto

import java.time.LocalDateTime

data class EventDetailResponse(
    val id: Long,
    val title: String,
    val expiredAt: LocalDateTime,
    val images: List<EventImageResponse>,
    val isSaved: Boolean,
    val createdAt: LocalDateTime
)
