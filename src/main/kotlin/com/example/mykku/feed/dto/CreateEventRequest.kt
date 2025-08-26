package com.example.mykku.feed.dto

import java.time.LocalDateTime

data class CreateEventRequest(
    val title: String,
    val isContest: Boolean = false,
    val expiredAt: LocalDateTime,
    val images: List<EventImageRequest> = emptyList(),
    val tags: List<String> = emptyList()
)

data class EventImageRequest(
    val url: String,
    val orderIndex: Int
)