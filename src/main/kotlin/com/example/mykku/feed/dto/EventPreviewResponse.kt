package com.example.mykku.feed.dto

import com.example.mykku.feed.domain.BasicEvent
import java.util.*

data class EventPreviewResponse(
    val id: UUID,
    val images: List<String>
) {
    constructor(event: BasicEvent) : this(
        id = event.id,
        images = event.basicEventImages.map { it.url }
    )
}
