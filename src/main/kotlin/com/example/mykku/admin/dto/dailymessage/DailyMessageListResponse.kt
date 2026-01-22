package com.example.mykku.admin.dto.dailymessage

import com.example.mykku.dailymessage.application.dto.DailyMessageSummaryResult
import java.time.LocalDate
import java.time.LocalDateTime

data class DailyMessageListResponse(
    val id: Long,
    val title: String,
    val content: String,
    val date: LocalDate,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(result: DailyMessageSummaryResult, createdAt: LocalDateTime): DailyMessageListResponse {
            return DailyMessageListResponse(
                id = result.id,
                title = result.title,
                content = result.content,
                date = result.date,
                createdAt = createdAt
            )
        }
    }
}
