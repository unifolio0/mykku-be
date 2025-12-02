package com.example.mykku.contest.dto

import com.example.mykku.contest.domain.ContestStatusType
import java.time.LocalDateTime

data class ContestDetailResponse(
    val id: Long,
    val title: String,
    val description: String?,
    val startedAt: LocalDateTime,
    val expiredAt: LocalDateTime,
    val status: ContestStatusType,
    val images: List<ContestImageResponse>,
    val tags: List<String>,
    val isSaved: Boolean,
    val createdAt: LocalDateTime
)
