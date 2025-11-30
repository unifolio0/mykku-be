package com.example.mykku.contest.dto

import java.time.LocalDateTime

data class ContestListResponse(
    val id: Long,
    val title: String,
    val description: String?,
    val expiredAt: LocalDateTime,
    val thumbnailUrl: String?,
    val tags: List<String>,
    val isSaved: Boolean
)
