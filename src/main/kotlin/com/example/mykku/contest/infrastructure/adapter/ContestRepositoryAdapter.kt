package com.example.mykku.contest.infrastructure.adapter

import com.example.mykku.contest.application.port.out.ContestRepositoryPort
import com.example.mykku.contest.domain.Contest
import com.example.mykku.contest.domain.ContestImage
import com.example.mykku.contest.domain.ContestTag
import com.example.mykku.contest.dto.ContestImageRequest
import com.example.mykku.contest.exception.ContestException
import com.example.mykku.contest.repository.ContestImageRepository
import com.example.mykku.contest.repository.ContestRepository
import com.example.mykku.contest.repository.ContestTagRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Component
class ContestRepositoryAdapter(
    private val contestRepository: ContestRepository,
    private val contestImageRepository: ContestImageRepository,
    private val contestTagRepository: ContestTagRepository
) : ContestRepositoryPort {

    override fun save(contest: Contest): Contest {
        return contestRepository.save(contest)
    }

    override fun saveImages(images: List<ContestImage>): List<ContestImage> {
        return contestImageRepository.saveAll(images)
    }

    override fun saveTags(tags: List<ContestTag>): List<ContestTag> {
        return contestTagRepository.saveAll(tags)
    }

    @Transactional
    override fun createContest(
        title: String,
        description: String?,
        startedAt: LocalDateTime,
        expiredAt: LocalDateTime,
        imageRequests: List<ContestImageRequest>,
        tagTitles: List<String>
    ): Triple<Contest, List<ContestImage>, List<ContestTag>> {
        if (imageRequests.size > Contest.IMAGE_MAX_COUNT) {
            throw ContestException.contestImageLimitExceeded()
        }

        val normalizedDistinctTags = tagTitles.asSequence()
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .distinct()
            .toList()

        if (normalizedDistinctTags.size > Contest.TAG_MAX_COUNT) {
            throw ContestException.contestTagLimitExceeded()
        }

        val contest = Contest(
            title = title,
            description = description,
            startedAt = startedAt,
            expiredAt = expiredAt
        )

        val savedContest = contestRepository.save(contest)

        val contestImages = imageRequests.map { imageRequest ->
            ContestImage(
                url = imageRequest.url,
                orderIndex = imageRequest.orderIndex,
                contest = savedContest
            )
        }
        val savedContestImages = contestImageRepository.saveAll(contestImages)

        val contestTags = normalizedDistinctTags.map { tagTitle ->
            ContestTag(
                title = tagTitle,
                contest = savedContest
            )
        }
        val savedContestTags = contestTagRepository.saveAll(contestTags)

        return Triple(savedContest, savedContestImages, savedContestTags)
    }
}
