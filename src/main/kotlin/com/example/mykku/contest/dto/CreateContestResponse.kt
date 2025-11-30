package com.example.mykku.contest.dto

import java.time.LocalDateTime

data class CreateContestResponse(
    val id: Long,
    val title: String,
    val expiredAt: LocalDateTime,
    val images: List<ContestImageResponse>,
    val tags: List<String>,
    val createdAt: LocalDateTime
)
