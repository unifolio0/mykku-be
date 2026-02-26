package com.example.mykku.event.application.dto

import com.example.mykku.event.domain.vo.EventSortType
import com.example.mykku.event.domain.vo.EventStatusType
import java.time.LocalDateTime

data class CreateEventCommand(
    val title: String,
    val description: String?,
    val startedAt: LocalDateTime,
    val expiredAt: LocalDateTime,
    val thumbnailUrl: String,
    val images: List<EventImageCommand>
)

data class EventImageCommand(
    val url: String,
    val orderIndex: Int
)

data class EventListQuery(
    val status: EventStatusType,
    val sortType: EventSortType,
    val page: Int,
    val size: Int,
    val memberId: String
)
