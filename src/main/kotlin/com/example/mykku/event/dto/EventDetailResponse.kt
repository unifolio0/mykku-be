package com.example.mykku.event.dto

import com.example.mykku.event.domain.EventStatusType
import java.time.LocalDateTime

data class EventDetailResponse(
    val id: Long,
    val title: String,
    val description: String?,
    val startedAt: LocalDateTime,
    val expiredAt: LocalDateTime,
    val status: EventStatusType,
    val images: List<EventImageResponse>,
    val isSaved: Boolean,
    val createdAt: LocalDateTime
)
