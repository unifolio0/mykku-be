package com.example.mykku.event.adapter.input.web

import com.example.mykku.event.application.dto.CreateEventResult
import com.example.mykku.event.application.dto.EventDetailResult
import com.example.mykku.event.application.dto.EventImageResult
import com.example.mykku.event.application.dto.EventListResult
import com.example.mykku.event.application.dto.EventPreviewResult
import com.example.mykku.event.application.dto.PagedEventsResult
import com.example.mykku.event.domain.vo.EventStatusType
import java.time.LocalDateTime

data class CreateEventResponse(
    val id: Long,
    val title: String,
    val description: String?,
    val startedAt: LocalDateTime,
    val expiredAt: LocalDateTime,
    val thumbnailUrl: String,
    val images: List<EventImageResponse>,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(result: CreateEventResult): CreateEventResponse {
            return CreateEventResponse(
                id = result.id,
                title = result.title,
                description = result.description,
                startedAt = result.startedAt,
                expiredAt = result.expiredAt,
                thumbnailUrl = result.thumbnailUrl,
                images = result.images.map { EventImageResponse.from(it) },
                createdAt = result.createdAt
            )
        }
    }
}

data class EventImageResponse(
    val url: String,
    val orderIndex: Int
) {
    companion object {
        fun from(result: EventImageResult): EventImageResponse {
            return EventImageResponse(
                url = result.url,
                orderIndex = result.orderIndex
            )
        }
    }
}

data class EventListResponse(
    val id: Long,
    val title: String,
    val startedAt: LocalDateTime,
    val expiredAt: LocalDateTime,
    val status: EventStatusType,
    val thumbnailUrl: String,
    val isSaved: Boolean
) {
    companion object {
        fun from(result: EventListResult): EventListResponse {
            return EventListResponse(
                id = result.id,
                title = result.title,
                startedAt = result.startedAt,
                expiredAt = result.expiredAt,
                status = result.status,
                thumbnailUrl = result.thumbnailUrl,
                isSaved = result.isSaved
            )
        }
    }
}

data class PagedEventsResponse(
    val content: List<EventListResponse>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
    val isLast: Boolean
) {
    companion object {
        fun from(result: PagedEventsResult): PagedEventsResponse {
            return PagedEventsResponse(
                content = result.content.map { EventListResponse.from(it) },
                page = result.page,
                size = result.size,
                totalElements = result.totalElements,
                totalPages = result.totalPages,
                isLast = result.isLast
            )
        }
    }
}

data class EventDetailResponse(
    val id: Long,
    val title: String,
    val description: String?,
    val startedAt: LocalDateTime,
    val expiredAt: LocalDateTime,
    val status: EventStatusType,
    val thumbnailUrl: String,
    val images: List<EventImageResponse>,
    val isSaved: Boolean,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(result: EventDetailResult): EventDetailResponse {
            return EventDetailResponse(
                id = result.id,
                title = result.title,
                description = result.description,
                startedAt = result.startedAt,
                expiredAt = result.expiredAt,
                status = result.status,
                thumbnailUrl = result.thumbnailUrl,
                images = result.images.map { EventImageResponse.from(it) },
                isSaved = result.isSaved,
                createdAt = result.createdAt
            )
        }
    }
}

data class EventPreviewResponse(
    val id: Long,
    val thumbnailUrl: String,
    val images: List<String>
) {
    companion object {
        fun from(result: EventPreviewResult): EventPreviewResponse {
            return EventPreviewResponse(
                id = result.id,
                thumbnailUrl = result.thumbnailUrl,
                images = result.images
            )
        }
    }
}
