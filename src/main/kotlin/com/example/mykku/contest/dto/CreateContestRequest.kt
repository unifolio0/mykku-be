package com.example.mykku.contest.dto

import java.time.LocalDateTime

data class CreateContestRequest(
    val title: String,
    val expiredAt: LocalDateTime,
    val images: List<ContestImageRequest> = emptyList(),
    val tags: List<String> = emptyList()
)
