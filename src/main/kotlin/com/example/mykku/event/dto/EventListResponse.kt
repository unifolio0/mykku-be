package com.example.mykku.event.dto

import java.time.LocalDateTime

data class EventListResponse(
    val id: Long,
    val title: String,
    val description: String?,
    val expiredAt: LocalDateTime,
    val thumbnailUrl: String?,
    val isSaved: Boolean
)
