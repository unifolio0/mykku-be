package com.example.mykku.feed.dto

import com.example.mykku.feed.domain.Event
import com.example.mykku.feed.domain.EventImage

data class EventPreviewResponse(
    val id: Long,
    val images: List<String>
) {
    constructor(event: Event, eventImages: List<EventImage> = emptyList()) : this(
        id = event.id!!,
        images = eventImages.map { it.url }
    )
}
