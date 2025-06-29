package com.example.mykku.dailymessage.dto

import java.time.LocalDate

data class DailyMessageSummaryResponse(
    val id: Long,
    val title: String,
    val content: String,
    val date: LocalDate
)
