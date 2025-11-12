package com.example.mykku.feed.dto

import java.time.LocalDateTime

data class EventListResponse(
    val id: Long,
    val title: String,
    val isContest: Boolean,
    val expiredAt: LocalDateTime,
    val thumbnailUrl: String?,
    val tags: List<String>,
    val isSaved: Boolean
)