package com.example.mykku.event.dto

import com.example.mykku.event.domain.Event
import com.example.mykku.event.domain.EventImage

data class EventPreviewResponse(
    val id: Long,
    val images: List<String>
) {
    constructor(event: Event, eventImages: List<EventImage> = emptyList()) : this(
        id = event.id!!,
        images = eventImages.map { it.url }
    )
}
