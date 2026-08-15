package com.example.mykku.event.adapter.input.web

import com.example.mykku.event.application.dto.CreateEventResult
import com.example.mykku.event.application.dto.EventDetailResult
import com.example.mykku.event.application.dto.EventImageResult
import com.example.mykku.event.application.dto.EventListResult
import com.example.mykku.event.application.dto.EventPreviewResult
import com.example.mykku.event.application.dto.EventWinnerAnnouncementResult
import com.example.mykku.event.application.dto.MyParticipatedEventResult
import com.example.mykku.event.application.dto.PagedEventsResult
import com.example.mykku.event.application.dto.PagedMyParticipatedEventsResult
import com.example.mykku.event.domain.vo.EventStatusType
import com.example.mykku.event.domain.vo.EventWinnerStatus
import java.time.LocalDate
import java.time.LocalDateTime

data class CreateEventResponse(
    val id: Long,
    val title: String,
    val subTitle: String?,
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
                subTitle = result.subTitle,
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
    val subTitle: String?,
    val description: String?,
    val startedAt: LocalDateTime,
    val expiredAt: LocalDateTime,
    val status: EventStatusType,
    val thumbnailUrl: String
) {
    companion object {
        fun from(result: EventListResult): EventListResponse {
            return EventListResponse(
                id = result.id,
                title = result.title,
                subTitle = result.subTitle,
                description = result.description,
                startedAt = result.startedAt,
                expiredAt = result.expiredAt,
                status = result.status,
                thumbnailUrl = result.thumbnailUrl
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
    val subTitle: String?,
    val description: String?,
    val startedAt: LocalDateTime,
    val expiredAt: LocalDateTime,
    val status: EventStatusType,
    val thumbnailUrl: String,
    val images: List<EventImageResponse>,
    val createdAt: LocalDateTime,
    val isWinner: Boolean
) {
    companion object {
        fun from(result: EventDetailResult): EventDetailResponse {
            return EventDetailResponse(
                id = result.id,
                title = result.title,
                subTitle = result.subTitle,
                description = result.description,
                startedAt = result.startedAt,
                expiredAt = result.expiredAt,
                status = result.status,
                thumbnailUrl = result.thumbnailUrl,
                images = result.images.map { EventImageResponse.from(it) },
                createdAt = result.createdAt,
                isWinner = result.isWinner
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

data class MyParticipatedEventResponse(
    val id: Long,
    val title: String,
    val startedAt: LocalDateTime,
    val expiredAt: LocalDateTime,
    val status: EventStatusType,
    val thumbnailUrl: String,
    val winnerStatus: EventWinnerStatus
) {
    companion object {
        fun from(result: MyParticipatedEventResult): MyParticipatedEventResponse {
            return MyParticipatedEventResponse(
                id = result.id,
                title = result.title,
                startedAt = result.startedAt,
                expiredAt = result.expiredAt,
                status = result.status,
                thumbnailUrl = result.thumbnailUrl,
                winnerStatus = result.winnerStatus
            )
        }
    }
}

data class PagedMyParticipatedEventsResponse(
    val content: List<MyParticipatedEventResponse>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
    val isLast: Boolean
) {
    companion object {
        fun from(result: PagedMyParticipatedEventsResult): PagedMyParticipatedEventsResponse {
            return PagedMyParticipatedEventsResponse(
                content = result.content.map { MyParticipatedEventResponse.from(it) },
                page = result.page,
                size = result.size,
                totalElements = result.totalElements,
                totalPages = result.totalPages,
                isLast = result.isLast
            )
        }
    }
}

data class EventWinnerAnnouncementResponse(
    val eventId: Long,
    val eventTitle: String,
    val title: String,
    val content: String,
    val announcedAt: LocalDate
) {
    companion object {
        fun from(result: EventWinnerAnnouncementResult): EventWinnerAnnouncementResponse {
            return EventWinnerAnnouncementResponse(
                eventId = result.eventId,
                eventTitle = result.eventTitle,
                title = result.title,
                content = result.content,
                announcedAt = result.announcedAt
            )
        }
    }
}
