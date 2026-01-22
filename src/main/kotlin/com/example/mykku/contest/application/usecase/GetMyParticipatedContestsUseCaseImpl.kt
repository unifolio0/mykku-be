package com.example.mykku.contest.application.usecase

import com.example.mykku.common.util.PageableValidator
import com.example.mykku.contest.application.dto.ContestListResult
import com.example.mykku.contest.application.dto.PagedContestsResult
import com.example.mykku.contest.application.port.input.GetMyParticipatedContestsUseCase
import com.example.mykku.contest.application.port.output.ContestImageRepository
import com.example.mykku.contest.application.port.output.ContestParticipationRepository
import com.example.mykku.contest.application.port.output.ContestTagRepository
import com.example.mykku.contest.domain.entity.Contest
import com.example.mykku.member.tool.MemberReader
import com.example.mykku.scrap.tool.SaveContestReader
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetMyParticipatedContestsUseCaseImpl(
    private val contestParticipationRepository: ContestParticipationRepository,
    private val contestImageRepository: ContestImageRepository,
    private val contestTagRepository: ContestTagRepository,
    private val saveContestReader: SaveContestReader,
    private val memberReader: MemberReader
) : GetMyParticipatedContestsUseCase {

    @Transactional(readOnly = true)
    override fun execute(memberId: Long, page: Int, size: Int): PagedContestsResult {
        val pageable = PageableValidator.validateAndCreate(page, size)
        val member = memberReader.getMemberById(memberId)

        val contestPage = contestParticipationRepository.findContestsByMemberId(memberId, pageable)

        val contestIds = contestPage.content.map { it.id }
        val imagesByContestId = contestImageRepository.findByContestIds(contestIds)
            .groupBy { it.contestId.value }
        val tagsByContestId = contestTagRepository.findByContestIds(contestIds)
            .groupBy { it.contestId.value }
        val savedContestIds = saveContestReader.getSavedContestIdsByContestIds(
            member,
            contestIds.map { it.value }
        )

        val contestListResults = contestPage.content.map { contest ->
            toContestListResult(contest, imagesByContestId, tagsByContestId, savedContestIds)
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
        tagsByContestId: Map<Long, List<com.example.mykku.contest.domain.entity.ContestTag>>,
        savedContestIds: Set<Long>
    ): ContestListResult {
        val images = imagesByContestId[contest.id.value] ?: emptyList()
        val tags = tagsByContestId[contest.id.value] ?: emptyList()

        return ContestListResult(
            id = contest.id.value,
            title = contest.title,
            startedAt = contest.startedAt,
            expiredAt = contest.expiredAt,
            status = contest.status,
            thumbnailUrl = images.sortedBy { it.orderIndex }.firstOrNull()?.url,
            tags = tags.map { it.title },
            isSaved = savedContestIds.contains(contest.id.value)
        )
    }
}
