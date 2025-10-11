package com.example.mykku.scrap.tool

import com.example.mykku.feed.domain.Feed
import com.example.mykku.member.domain.Member
import com.example.mykku.scrap.domain.Folder
import com.example.mykku.scrap.domain.SaveFeed
import com.example.mykku.scrap.exception.ScrapException
import com.example.mykku.scrap.repository.SaveFeedRepository
import org.springframework.stereotype.Component

@Component
class SaveFeedWriter(
    private val saveFeedRepository: SaveFeedRepository,
    private val saveFeedReader: SaveFeedReader
) {
    fun saveFeed(member: Member, feed: Feed, folder: Folder): SaveFeed {
        if (saveFeedReader.isSaved(member, feed)) {
            throw ScrapException.saveFeedAlreadyExists()
        }

        val saveFeed = SaveFeed(
            member = member,
            feed = feed,
            folder = folder
        )

        return saveFeedRepository.save(saveFeed)
    }

    fun updateFolder(member: Member, feed: Feed, folder: Folder) {
        val saveFeed = saveFeedRepository.findByMemberAndFeed(member, feed)
            ?: throw ScrapException.saveFeedNotFound()

        saveFeed.updateFolder(folder)
        saveFeedRepository.save(saveFeed)
    }

    fun unsaveFeed(member: Member, feed: Feed) {
        if (!saveFeedReader.isSaved(member, feed)) {
            throw ScrapException.saveFeedNotFound()
        }

        saveFeedRepository.deleteByMemberAndFeed(member, feed)
    }
}
