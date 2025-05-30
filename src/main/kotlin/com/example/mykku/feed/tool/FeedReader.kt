package com.example.mykku.feed.tool

import com.example.mykku.feed.dto.FeedPreviewResponse
import com.example.mykku.feed.repository.FeedRepository
import org.springframework.stereotype.Service

@Service
class FeedReader(
    private val feedRepository: FeedRepository,
) {
    fun getFeedPreviews(): List<FeedPreviewResponse> {
        feedRepository.findAll()
            .map { feed -> FeedPreviewResponse(feed) }
            .take(5)
        return emptyList()
    }
}
