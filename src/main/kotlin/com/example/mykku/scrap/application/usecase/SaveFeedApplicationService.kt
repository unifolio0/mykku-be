package com.example.mykku.scrap.application.usecase

import com.example.mykku.scrap.application.dto.GetSavedFeedsQuery
import com.example.mykku.scrap.application.dto.SaveFeedCommand
import com.example.mykku.scrap.application.dto.SaveFeedResult
import com.example.mykku.scrap.application.dto.UnsaveFeedCommand
import com.example.mykku.scrap.application.dto.UpdateSaveFeedFolderCommand
import com.example.mykku.scrap.application.port.input.SaveFeedUseCase
import com.example.mykku.scrap.application.port.output.FolderPort
import com.example.mykku.scrap.application.port.output.SaveFeedPort
import com.example.mykku.scrap.domain.entity.SaveFeedEntity
import com.example.mykku.scrap.exception.ScrapException
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SaveFeedApplicationService(
    private val saveFeedPort: SaveFeedPort,
    private val folderPort: FolderPort
) : SaveFeedUseCase {

    @Transactional
    override fun saveFeed(command: SaveFeedCommand) {
        validateFolderOwnership(command.memberId, command.folderId)

        if (saveFeedPort.existsByMemberIdAndFeedId(command.memberId, command.feedId)) {
            throw ScrapException.saveFeedAlreadyExists()
        }

        val saveFeed = SaveFeedEntity.create(
            memberId = command.memberId,
            feedId = command.feedId,
            folderId = command.folderId
        )

        saveFeedPort.save(saveFeed)
    }

    @Transactional
    override fun unsaveFeed(command: UnsaveFeedCommand) {
        if (!saveFeedPort.existsByMemberIdAndFeedId(command.memberId, command.feedId)) {
            throw ScrapException.saveFeedNotFound()
        }
        saveFeedPort.deleteByMemberIdAndFeedId(command.memberId, command.feedId)
    }

    @Transactional
    override fun updateSaveFeedFolder(command: UpdateSaveFeedFolderCommand) {
        validateFolderOwnership(command.memberId, command.folderId)

        val saveFeed = saveFeedPort.findByMemberIdAndFeedId(command.memberId, command.feedId)
            ?: throw ScrapException.saveFeedNotFound()

        saveFeed.updateFolder(command.folderId)
        saveFeedPort.save(saveFeed)
    }

    @Transactional(readOnly = true)
    override fun getSavedFeeds(query: GetSavedFeedsQuery): Page<SaveFeedResult> {
        if (query.folderId != null) {
            validateFolderOwnership(query.memberId, query.folderId)
        }

        val pageable = PageRequest.of(
            query.page,
            query.size,
            Sort.by(Sort.Direction.DESC, "createdAt")
        )

        return saveFeedPort.findByMemberIdAndFolderId(query.memberId, query.folderId, pageable)
    }

    @Transactional(readOnly = true)
    override fun isSaved(memberId: Long, feedId: Long): Boolean {
        return saveFeedPort.existsByMemberIdAndFeedId(memberId, feedId)
    }

    @Transactional(readOnly = true)
    override fun getSavedFeedIds(memberId: Long, feedIds: List<Long>): Set<Long> {
        return saveFeedPort.findByMemberIdAndFeedIdIn(memberId, feedIds)
            .map { it.feedId }
            .toSet()
    }

    @Transactional
    override fun deleteAllByFeedId(feedId: Long) {
        saveFeedPort.deleteAllByFeedId(feedId)
    }

    private fun validateFolderOwnership(memberId: Long, folderId: Long) {
        folderPort.findByMemberIdAndId(memberId, folderId)
            ?: throw ScrapException.folderNotFound()
    }
}
