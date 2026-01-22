package com.example.mykku.scrap.application.dto

import java.time.LocalDateTime

data class FolderResult(
    val id: Long,
    val name: String,
    val description: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

data class FoldersResult(
    val folders: List<FolderResult>
)

data class SaveFeedResult(
    val id: Long,
    val feedId: Long,
    val folderId: Long,
    val folderName: String
)

data class SaveDailyMessageResult(
    val id: Long,
    val dailyMessageId: Long
)

data class SaveEventResult(
    val id: Long,
    val eventId: Long
)

data class SaveFanNoteResult(
    val id: Long,
    val fanNoteId: Long
)
