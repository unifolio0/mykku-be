package com.example.mykku.feed.dto

import java.time.LocalDateTime

data class EventDetailResponse(
    val id: Long,
    val title: String,
    val isContest: Boolean,
    val expiredAt: LocalDateTime,
    val images: List<EventImageResponse>,
    val tags: List<String>,
    val isSaved: Boolean,
    val createdAt: LocalDateTime
)