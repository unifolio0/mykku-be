package com.example.mykku.scrap.application.dto

data class SaveFanNoteCommand(
    val memberId: Long,
    val fanNoteId: Long
)

data class UnsaveFanNoteCommand(
    val memberId: Long,
    val fanNoteId: Long
)

data class GetSavedFanNotesQuery(
    val memberId: Long,
    val page: Int,
    val size: Int
)
