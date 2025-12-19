package com.example.mykku.contest.application.port.out

import com.example.mykku.contest.domain.model.ContestId
import java.time.LocalDateTime

data class ContestSummary(
    val id: Long,
    val title: String,
    val startedAt: LocalDateTime,
    val expiredAt: LocalDateTime
)

interface ContestQueryPort {
    fun findSummaryById(id: ContestId): ContestSummary?
    fun existsById(id: ContestId): Boolean
}
