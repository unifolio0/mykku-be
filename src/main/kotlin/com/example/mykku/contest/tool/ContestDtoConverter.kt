package com.example.mykku.contest.tool

import com.example.mykku.contest.domain.Contest
import com.example.mykku.contest.domain.ContestImage
import com.example.mykku.contest.domain.ContestTag
import com.example.mykku.contest.dto.ContestDetailResponse
import com.example.mykku.contest.dto.ContestImageResponse
import com.example.mykku.contest.dto.ContestListResponse
import org.springframework.stereotype.Component

@Component
class ContestDtoConverter {

    fun toContestListResponse(
        contest: Contest,
        images: List<ContestImage>,
        tags: List<ContestTag>,
        isSaved: Boolean
    ): ContestListResponse {
        val sortedImages = sortImagesByOrder(images)
        return ContestListResponse(
            id = contest.id!!,
            title = contest.title,
            expiredAt = contest.expiredAt,
            thumbnailUrl = sortedImages.firstOrNull()?.url,
            tags = tags.map { it.title },
            isSaved = isSaved
        )
    }

    fun toContestDetailResponse(
        contest: Contest,
        images: List<ContestImage>,
        tags: List<ContestTag>,
        isSaved: Boolean
    ): ContestDetailResponse {
        val sortedImages = sortImagesByOrder(images)
        return ContestDetailResponse(
            id = contest.id!!,
            title = contest.title,
            expiredAt = contest.expiredAt,
            images = sortedImages.map { ContestImageResponse(url = it.url, orderIndex = it.orderIndex) },
            tags = tags.map { it.title },
            isSaved = isSaved,
            createdAt = contest.createdAt
        )
    }

    private fun sortImagesByOrder(images: List<ContestImage>): List<ContestImage> {
        return images.sortedBy { it.orderIndex }
    }
}
