package com.example.mykku.dailymessage.dto

import java.time.LocalDate

data class DailyMessageResponse(
    val id: Long,
    val title: String,
    val content: String,
    val date: LocalDate
)
