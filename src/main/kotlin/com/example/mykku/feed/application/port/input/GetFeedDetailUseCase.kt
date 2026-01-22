package com.example.mykku.feed.application.port.input

import com.example.mykku.feed.application.dto.FeedDetailResult
import com.example.mykku.feed.application.dto.GetFeedDetailQuery

interface GetFeedDetailUseCase {
    fun execute(query: GetFeedDetailQuery): FeedDetailResult
}
