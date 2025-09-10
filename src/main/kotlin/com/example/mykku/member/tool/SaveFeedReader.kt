package com.example.mykku.member.tool

import com.example.mykku.feed.domain.Feed
import com.example.mykku.member.repository.SaveFeedRepository
import org.springframework.stereotype.Component

@Component
class SaveFeedReader(
    private val saveFeedRepository: SaveFeedRepository,
) {
    fun isSaved(memberId: String, feed: Feed): Boolean {
        return saveFeedRepository.existsByMemberIdAndFeed(memberId, feed)
    }
    
    fun getSavedFeedsByMember(memberId: String, feeds: List<Feed>): Set<Long> {
        val feedIds = feeds.mapNotNull { it.id }
        return saveFeedRepository.findByMemberIdAndFeedIdIn(memberId, feedIds)
            .map { it.feed.id!! }
            .toSet()
    }
}
