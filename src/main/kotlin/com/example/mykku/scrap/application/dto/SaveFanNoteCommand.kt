package com.example.mykku.scrap.application.dto

data class SaveFanNoteCommand(
    val memberId: String,
    val fanNoteId: Long
)

data class UnsaveFanNoteCommand(
    val memberId: String,
    val fanNoteId: Long
)

data class GetSavedFanNotesQuery(
    val memberId: String,
    val page: Int,
    val size: Int
)
