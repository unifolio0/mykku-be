package com.example.mykku.scrap.application.port.input

import com.example.mykku.scrap.application.dto.GetSavedFeedsQuery
import com.example.mykku.scrap.application.dto.SaveFeedCommand
import com.example.mykku.scrap.application.dto.SaveFeedResult
import com.example.mykku.scrap.application.dto.UnsaveFeedCommand
import com.example.mykku.scrap.application.dto.UpdateSaveFeedFolderCommand
import org.springframework.data.domain.Page

interface SaveFeedUseCase {
    fun saveFeed(command: SaveFeedCommand)
    fun unsaveFeed(command: UnsaveFeedCommand)
    fun updateSaveFeedFolder(command: UpdateSaveFeedFolderCommand)
    fun getSavedFeeds(query: GetSavedFeedsQuery): Page<SaveFeedResult>
    fun isSaved(memberId: String, feedId: Long): Boolean
    fun getSavedFeedIds(memberId: String, feedIds: List<Long>): Set<Long>
    fun deleteAllByFeedId(feedId: Long)
}
