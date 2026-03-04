package com.example.mykku.scrap.application.dto

data class SaveDailyMessageCommand(
    val memberId: Long,
    val dailyMessageId: Long
)

data class UnsaveDailyMessageCommand(
    val memberId: Long,
    val dailyMessageId: Long
)

data class GetSavedDailyMessagesQuery(
    val memberId: Long,
    val page: Int,
    val size: Int
)
