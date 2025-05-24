package com.example.mykku.home.dto

import com.example.mykku.feed.dto.EventPreviewResponse

data class HomeResponse(
    val dailyMessage: String,
    val events: List<EventPreviewResponse>
)
