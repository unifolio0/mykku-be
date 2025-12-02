package com.example.mykku.event.dto

import com.example.mykku.event.domain.EventStatusType
import java.time.LocalDateTime

data class EventListResponse(
    val id: Long,
    val title: String,
    val startedAt: LocalDateTime,
    val expiredAt: LocalDateTime,
    val status: EventStatusType,
    val thumbnailUrl: String?,
    val isSaved: Boolean
)
