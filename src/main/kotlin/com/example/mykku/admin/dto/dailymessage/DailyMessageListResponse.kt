package com.example.mykku.admin.dto.dailymessage

import com.example.mykku.dailymessage.domain.DailyMessage
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
        fun from(dailyMessage: DailyMessage): DailyMessageListResponse {
            return DailyMessageListResponse(
                id = dailyMessage.id!!,
                title = dailyMessage.title,
                content = dailyMessage.content,
                date = dailyMessage.date,
                createdAt = dailyMessage.createdAt
            )
        }
    }
}
