package com.example.mykku.scrap.dto

import com.example.mykku.scrap.domain.SaveFeed
import org.springframework.data.domain.Page

data class SaveFeedRequest(
    val folderId: Long
)

data class UpdateSaveFeedFolderRequest(
    val folderId: Long
)

data class SaveFeedResponse(
    val id: Long,
    val feedId: Long,
    val folderId: Long,
    val folderName: String
) {
    companion object {
        fun from(saveFeed: SaveFeed): SaveFeedResponse {
            return SaveFeedResponse(
                id = saveFeed.id!!,
                feedId = saveFeed.feed.id!!,
                folderId = saveFeed.folder.id!!,
                folderName = saveFeed.folder.name
            )
        }

        fun fromPage(page: Page<SaveFeed>): Page<SaveFeedResponse> {
            return page.map { from(it) }
        }
    }
}
