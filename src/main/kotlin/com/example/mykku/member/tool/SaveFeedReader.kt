package com.example.mykku.member.tool

import com.example.mykku.feed.domain.Feed
import com.example.mykku.member.domain.Member
import com.example.mykku.member.repository.SaveFeedRepository
import org.springframework.stereotype.Component

@Component
class SaveFeedReader(
    private val saveFeedRepository: SaveFeedRepository,
) {
    fun isSaved(member: Member, feed: Feed): Boolean {
        return saveFeedRepository.existsByMemberAndFeed(member, feed)
    }
}
