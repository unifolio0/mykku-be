package com.example.mykku.home.dto

import com.example.mykku.dailymessage.dto.DailyMessageSummaryResponse
import com.example.mykku.feed.dto.ContestWinnersResponse
import com.example.mykku.feed.dto.EventPreviewResponse
import com.example.mykku.feed.dto.FeedPreviewResponse

data class HomeResponse(
    val dailyMessage: DailyMessageSummaryResponse,
    val events: List<EventPreviewResponse>,
    val feeds: List<FeedPreviewResponse>,
    val contests: List<ContestWinnersResponse>
)
