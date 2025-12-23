package com.example.mykku.contest.application.port.out

import com.example.mykku.contest.domain.Contest
import com.example.mykku.contest.domain.ContestImage
import com.example.mykku.contest.domain.ContestTag
import com.example.mykku.contest.dto.ContestImageRequest
import java.time.LocalDateTime

interface ContestRepositoryPort {
    fun save(contest: Contest): Contest
    fun saveImages(images: List<ContestImage>): List<ContestImage>
    fun saveTags(tags: List<ContestTag>): List<ContestTag>
    fun createContest(
        title: String,
        description: String?,
        startedAt: LocalDateTime,
        expiredAt: LocalDateTime,
        imageRequests: List<ContestImageRequest>,
        tagTitles: List<String>
    ): Triple<Contest, List<ContestImage>, List<ContestTag>>
}
