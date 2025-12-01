package com.example.mykku.contest.dto

import com.example.mykku.contest.domain.Contest
import com.example.mykku.contest.domain.ContestImage

data class ContestPreviewResponse(
    val id: Long,
    val title: String,
    val thumbnailUrl: String?
) {
    constructor(contest: Contest, images: List<ContestImage>) : this(
        id = contest.id!!,
        title = contest.title,
        thumbnailUrl = images.sortedBy { it.orderIndex }.firstOrNull()?.url
    )
}
