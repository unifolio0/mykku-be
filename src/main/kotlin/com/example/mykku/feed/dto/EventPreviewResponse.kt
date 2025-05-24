package com.example.mykku.feed.dto

import com.example.mykku.feed.domain.Tag

data class EventPreviewResponse(
    val id: Long,
    val images: List<String>
) {
    constructor(tag: Tag) : this(
        id = tag.id,
        images = tag.tagImages.map { it.url }
    )
}
