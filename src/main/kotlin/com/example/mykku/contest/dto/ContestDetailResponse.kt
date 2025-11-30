package com.example.mykku.contest.dto

import java.time.LocalDateTime

data class ContestDetailResponse(
    val id: Long,
    val title: String,
    val expiredAt: LocalDateTime,
    val images: List<ContestImageResponse>,
    val tags: List<String>,
    val isSaved: Boolean,
    val createdAt: LocalDateTime
)
