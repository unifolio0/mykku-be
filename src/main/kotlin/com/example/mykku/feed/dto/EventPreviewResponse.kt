package com.example.mykku.feed.dto

import com.example.mykku.feed.domain.BasicEvent

data class EventPreviewResponse(
    val id: Long,
    val images: List<String>
) {
    constructor(event: BasicEvent) : this(
        id = event.id!!,
        images = event.basicEventImages.map { it.url }
    )
}
