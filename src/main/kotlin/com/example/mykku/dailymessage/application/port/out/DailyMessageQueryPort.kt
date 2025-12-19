package com.example.mykku.dailymessage.application.port.out

import com.example.mykku.dailymessage.domain.model.DailyMessageId
import java.time.LocalDate

data class DailyMessageSummary(
    val id: Long,
    val title: String,
    val content: String,
    val date: LocalDate
)

interface DailyMessageQueryPort {
    fun findSummaryById(id: DailyMessageId): DailyMessageSummary?
    fun existsById(id: DailyMessageId): Boolean
}
