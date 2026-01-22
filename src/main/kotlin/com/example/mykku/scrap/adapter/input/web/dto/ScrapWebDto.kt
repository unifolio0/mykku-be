package com.example.mykku.scrap.adapter.input.web.dto

import com.example.mykku.scrap.application.dto.FolderResult
import com.example.mykku.scrap.application.dto.FoldersResult
import com.example.mykku.scrap.application.dto.SaveDailyMessageResult
import com.example.mykku.scrap.application.dto.SaveEventResult
import com.example.mykku.scrap.application.dto.SaveFanNoteResult
import com.example.mykku.scrap.application.dto.SaveFeedResult
import java.time.LocalDateTime

data class CreateFolderWebRequest(
    val name: String,
    val description: String? = null
)

data class UpdateFolderWebRequest(
    val name: String,
    val description: String? = null
)

data class FolderWebResponse(
    val id: Long,
    val name: String,
    val description: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun from(result: FolderResult): FolderWebResponse {
            return FolderWebResponse(
                id = result.id,
                name = result.name,
                description = result.description,
                createdAt = result.createdAt,
                updatedAt = result.updatedAt
            )
        }
    }
}

data class FoldersWebResponse(
    val folders: List<FolderWebResponse>
) {
    companion object {
        fun from(result: FoldersResult): FoldersWebResponse {
            return FoldersWebResponse(
                folders = result.folders.map { FolderWebResponse.from(it) }
            )
        }
    }
}

data class SaveFeedWebRequest(
    val folderId: Long
)

data class UpdateSaveFeedFolderWebRequest(
    val folderId: Long
)

data class SaveFeedWebResponse(
    val id: Long,
    val feedId: Long,
    val folderId: Long,
    val folderName: String
) {
    companion object {
        fun from(result: SaveFeedResult): SaveFeedWebResponse {
            return SaveFeedWebResponse(
                id = result.id,
                feedId = result.feedId,
                folderId = result.folderId,
                folderName = result.folderName
            )
        }
    }
}

data class SaveDailyMessageWebResponse(
    val id: Long,
    val dailyMessageId: Long
) {
    companion object {
        fun from(result: SaveDailyMessageResult): SaveDailyMessageWebResponse {
            return SaveDailyMessageWebResponse(
                id = result.id,
                dailyMessageId = result.dailyMessageId
            )
        }
    }
}

data class SaveEventWebResponse(
    val id: Long,
    val eventId: Long
) {
    companion object {
        fun from(result: SaveEventResult): SaveEventWebResponse {
            return SaveEventWebResponse(
                id = result.id,
                eventId = result.eventId
            )
        }
    }
}

data class SaveFanNoteWebResponse(
    val id: Long,
    val fanNoteId: Long
) {
    companion object {
        fun from(result: SaveFanNoteResult): SaveFanNoteWebResponse {
            return SaveFanNoteWebResponse(
                id = result.id,
                fanNoteId = result.fanNoteId
            )
        }
    }
}
