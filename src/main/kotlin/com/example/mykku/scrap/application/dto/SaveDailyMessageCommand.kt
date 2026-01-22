package com.example.mykku.scrap.application.dto

data class SaveDailyMessageCommand(
    val memberId: String,
    val dailyMessageId: Long
)

data class UnsaveDailyMessageCommand(
    val memberId: String,
    val dailyMessageId: Long
)

data class GetSavedDailyMessagesQuery(
    val memberId: String,
    val page: Int,
    val size: Int
)
