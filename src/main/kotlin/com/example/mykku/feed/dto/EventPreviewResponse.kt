package com.example.mykku.feed.dto

import com.example.mykku.feed.domain.Event

data class EventPreviewResponse(
    val id: Long,
    val images: List<String>
) {
    constructor(event: Event) : this(
        id = event.id!!,
        images = event.eventImages.map { it.url }
    )
}
