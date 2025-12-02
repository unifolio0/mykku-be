package com.example.mykku.contest.dto

import java.time.LocalDateTime

data class CreateContestRequest(
    val title: String,
    val description: String? = null,
    val startedAt: LocalDateTime,
    val expiredAt: LocalDateTime,
    val images: List<ContestImageRequest> = emptyList(),
    val tags: List<String> = emptyList()
)

data class ContestImageRequest(
    val url: String,
    val orderIndex: Int
)
