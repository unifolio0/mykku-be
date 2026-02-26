package com.example.mykku.contest.application.usecase

import com.example.mykku.common.util.PageableValidator
import com.example.mykku.contest.application.dto.ContestListResult
import com.example.mykku.contest.application.dto.PagedContestsResult
import com.example.mykku.contest.application.port.input.GetMyParticipatedContestsUseCase
import com.example.mykku.contest.application.port.output.ContestImageRepository
import com.example.mykku.contest.application.port.output.ContestParticipationRepository
import com.example.mykku.contest.application.port.output.ContestTagRepository
import com.example.mykku.contest.domain.entity.Contest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetMyParticipatedContestsUseCaseImpl(
    private val contestParticipationRepository: ContestParticipationRepository,
    private val contestImageRepository: ContestImageRepository,
    private val contestTagRepository: ContestTagRepository
) : GetMyParticipatedContestsUseCase {

    @Transactional(readOnly = true)
    override fun execute(memberId: String, page: Int, size: Int): PagedContestsResult {
        val pageable = PageableValidator.validateAndCreate(page, size)

        val contestPage = contestParticipationRepository.findContestsByMemberId(memberId, pageable)

        val contestIds = contestPage.content.map { it.id }
        val imagesByContestId = contestImageRepository.findByContestIds(contestIds)
            .groupBy { it.contestId.value }
        val tagsByContestId = contestTagRepository.findByContestIds(contestIds)
            .groupBy { it.contestId.value }

        val contestListResults = contestPage.content.map { contest ->
            toContestListResult(contest, imagesByContestId, tagsByContestId)
        }

        return PagedContestsResult(
            content = contestListResults,
            page = contestPage.number,
            size = contestPage.size,
            totalElements = contestPage.totalElements,
            totalPages = contestPage.totalPages,
            isLast = contestPage.isLast
        )
    }

    private fun toContestListResult(
        contest: Contest,
        imagesByContestId: Map<Long, List<com.example.mykku.contest.domain.entity.ContestImage>>,
        tagsByContestId: Map<Long, List<com.example.mykku.contest.domain.entity.ContestTag>>
    ): ContestListResult {
        val tags = tagsByContestId[contest.id.value] ?: emptyList()

        return ContestListResult(
            id = contest.id.value,
            title = contest.title,
            startedAt = contest.startedAt,
            expiredAt = contest.expiredAt,
            status = contest.status,
            thumbnailUrl = contest.thumbnailUrl,
            tags = tags.map { it.title },
            isSaved = false
        )
    }
}
