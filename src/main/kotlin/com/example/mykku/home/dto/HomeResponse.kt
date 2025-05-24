package com.example.mykku.home.dto

import com.example.mykku.feed.dto.EventPreviewResponse
import com.example.mykku.feed.dto.FeedPreviewResponse

data class HomeResponse(
    val dailyMessage: String,
    val events: List<EventPreviewResponse>,
    val feeds: List<FeedPreviewResponse>,
)
