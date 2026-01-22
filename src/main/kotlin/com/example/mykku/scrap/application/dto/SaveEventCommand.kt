package com.example.mykku.scrap.application.dto

data class SaveEventCommand(
    val memberId: String,
    val eventId: Long
)

data class UnsaveEventCommand(
    val memberId: String,
    val eventId: Long
)

data class GetSavedEventsQuery(
    val memberId: String,
    val page: Int,
    val size: Int
)
