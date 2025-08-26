package com.example.mykku.feed.dto

import java.time.LocalDateTime

data class CreateEventResponse(
    val id: Long,
    val title: String,
    val isContest: Boolean,
    val expiredAt: LocalDateTime,
    val images: List<EventImageResponse>,
    val tags: List<String>,
    val createdAt: LocalDateTime
)

data class EventImageResponse(
    val url: String,
    val orderIndex: Int
)