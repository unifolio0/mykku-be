package com.example.mykku.contest.dto

import java.time.LocalDateTime

data class CreateContestResponse(
    val id: Long,
    val title: String,
    val description: String?,
    val startedAt: LocalDateTime,
    val expiredAt: LocalDateTime,
    val images: List<ContestImageResponse>,
    val tags: List<String>,
    val createdAt: LocalDateTime
)

data class ContestImageResponse(
    val url: String,
    val orderIndex: Int
)
