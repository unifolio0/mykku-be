package com.example.mykku.scrap.application.dto

data class SaveEventCommand(
    val memberId: Long,
    val eventId: Long
)

data class UnsaveEventCommand(
    val memberId: Long,
    val eventId: Long
)

data class GetSavedEventsQuery(
    val memberId: Long,
    val page: Int,
    val size: Int
)
