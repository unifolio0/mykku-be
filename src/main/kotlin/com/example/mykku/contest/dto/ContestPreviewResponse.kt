package com.example.mykku.contest.dto

import com.example.mykku.contest.domain.Contest
import com.example.mykku.contest.domain.ContestImage

data class ContestPreviewResponse(
    val id: Long,
    val images: List<String>
) {
    constructor(contest: Contest, contestImages: List<ContestImage> = emptyList()) : this(
        id = contest.id!!,
        images = contestImages.map { it.url }
    )
}
