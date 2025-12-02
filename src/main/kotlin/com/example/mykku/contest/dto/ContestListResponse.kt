package com.example.mykku.contest.dto

import com.example.mykku.contest.domain.ContestStatusType
import java.time.LocalDateTime

data class ContestListResponse(
    val id: Long,
    val title: String,
    val startedAt: LocalDateTime,
    val expiredAt: LocalDateTime,
    val status: ContestStatusType,
    val thumbnailUrl: String?,
    val tags: List<String>,
    val isSaved: Boolean
)
