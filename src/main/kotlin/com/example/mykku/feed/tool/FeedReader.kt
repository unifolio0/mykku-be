package com.example.mykku.feed.tool

import com.example.mykku.feed.domain.Feed
import com.example.mykku.feed.dto.FeedPreviewResponse
import com.example.mykku.feed.repository.FeedRepository
import com.example.mykku.member.domain.Member
import org.springframework.stereotype.Component

@Component
class FeedReader(
    private val feedRepository: FeedRepository,
) {
    fun getFeedPreviews(): List<FeedPreviewResponse> {
        feedRepository.findAll()
            .map { feed -> FeedPreviewResponse(feed) }
            .take(5)
        return emptyList()
    }

    fun getFeedsByFollower(members: List<Member>): List<Feed> {
        return feedRepository.findAllByMemberIn(members)
    }
}
