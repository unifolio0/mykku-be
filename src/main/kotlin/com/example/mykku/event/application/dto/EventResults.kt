package com.example.mykku.event.application.dto

import com.example.mykku.event.domain.vo.EventStatusType
import java.time.LocalDateTime

data class CreateEventResult(
    val id: Long,
    val title: String,
    val description: String?,
    val startedAt: LocalDateTime,
    val expiredAt: LocalDateTime,
    val thumbnailUrl: String,
    val images: List<EventImageResult>,
    val createdAt: LocalDateTime
)

data class EventImageResult(
    val url: String,
    val orderIndex: Int
)

data class EventListResult(
    val id: Long,
    val title: String,
    val startedAt: LocalDateTime,
    val expiredAt: LocalDateTime,
    val status: EventStatusType,
    val thumbnailUrl: String
)

data class PagedEventsResult(
    val content: List<EventListResult>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
    val isLast: Boolean
)

data class EventDetailResult(
    val id: Long,
    val title: String,
    val description: String?,
    val startedAt: LocalDateTime,
    val expiredAt: LocalDateTime,
    val status: EventStatusType,
    val thumbnailUrl: String,
    val images: List<EventImageResult>,
    val createdAt: LocalDateTime
)

data class EventPreviewResult(
    val id: Long,
    val thumbnailUrl: String,
    val images: List<String>
)
