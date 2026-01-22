package com.example.mykku.feed.application.port.input

import com.example.mykku.feed.application.dto.FeedCommentsResult
import com.example.mykku.feed.application.dto.GetFeedCommentsQuery

interface GetFeedCommentsUseCase {
    fun execute(query: GetFeedCommentsQuery): FeedCommentsResult
}
