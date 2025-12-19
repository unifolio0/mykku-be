package com.example.mykku.contest

import com.example.mykku.common.util.PageableValidator
import com.example.mykku.contest.domain.Contest
import com.example.mykku.contest.domain.ContestSortType
import com.example.mykku.contest.domain.ContestStatusType
import com.example.mykku.contest.dto.*
import com.example.mykku.contest.application.port.out.ContestParticipationQueryPort
import com.example.mykku.contest.application.port.out.ContestQueryPort
import com.example.mykku.contest.application.port.out.ContestRepositoryPort
import com.example.mykku.contest.tool.ContestDtoConverter
import com.example.mykku.member.domain.Member
import com.example.mykku.scrap.application.port.out.SaveContestQueryPort
import org.springframework.data.domain.PageImpl
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class ContestService(
    private val contestRepositoryPort: ContestRepositoryPort,
    private val contestQueryPort: ContestQueryPort,
    private val contestParticipationQueryPort: ContestParticipationQueryPort,
    private val saveContestQueryPort: SaveContestQueryPort,
    private val contestDtoConverter: ContestDtoConverter
) {

    @Transactional
    fun createContest(request: CreateContestRequest): CreateContestResponse {
        val (contest, contestImages, contestTags) = contestRepositoryPort.createContest(
            title = request.title,
            description = request.description,
            startedAt = request.startedAt,
            expiredAt = request.expiredAt,
            imageRequests = request.images,
            tagTitles = request.tags
        )

        return CreateContestResponse(
            id = contest.id!!,
            title = contest.title,
            description = contest.description,
            startedAt = contest.startedAt,
            expiredAt = contest.expiredAt,
            images = contestImages
                .sortedBy { it.orderIndex }
                .map { ContestImageResponse(url = it.url, orderIndex = it.orderIndex) },
            tags = contestTags.map { it.title },
            createdAt = contest.createdAt
        )
    }

    @Transactional(readOnly = true)
    fun getContests(
        status: ContestStatusType,
        sortType: ContestSortType,
        page: Int,
        size: Int,
        member: Member
    ): PagedContestsResponse {
        val pageable = PageableValidator.validateAndCreate(page, size)
        val contestPage = contestQueryPort.getContestsWithPagination(status, sortType, pageable, LocalDateTime.now())

        val contestListResponses = convertToContestListResponses(contestPage.content, member)
        val responseMap = contestListResponses.associateBy { it.id }
        val responsePage = contestPage.map { contest ->
            responseMap[contest.id]!!
        }

        return PagedContestsResponse.from(responsePage)
    }

    private fun convertToContestListResponses(contests: List<Contest>, member: Member): List<ContestListResponse> {
        val imagesByContestId = contestQueryPort.getContestImages(contests)
        val tagsByContestId = contestQueryPort.getContestTags(contests)
        val savedContestIds = saveContestQueryPort.getSavedContestIds(member, contests)

        return contests.map { contest ->
            val images = imagesByContestId[contest.id!!] ?: emptyList()
            val tags = tagsByContestId[contest.id!!] ?: emptyList()
            val isSaved = savedContestIds.contains(contest.id)
            contestDtoConverter.toContestListResponse(contest, images, tags, isSaved)
        }
    }

    @Transactional(readOnly = true)
    fun getContestDetail(contestId: Long, member: Member): ContestDetailResponse {
        val contest = contestQueryPort.getContestByIdWithRelations(contestId)
        val images = contestQueryPort.getContestImages(listOf(contest))[contest.id] ?: emptyList()
        val tags = contestQueryPort.getContestTags(listOf(contest))[contest.id] ?: emptyList()
        val isSaved = saveContestQueryPort.isSaved(member, contest)

        return contestDtoConverter.toContestDetailResponse(contest, images, tags, isSaved)
    }

    @Transactional(readOnly = true)
    fun getMyParticipatedContests(member: Member, page: Int, size: Int): PagedContestsResponse {
        val pageable = PageableValidator.validateAndCreate(page, size)
        val contestPage = contestParticipationQueryPort.getParticipatedContests(member, pageable)

        val contestListResponses = convertToContestListResponses(contestPage.content, member)
        val responsePage = PageImpl(contestListResponses, contestPage.pageable, contestPage.totalElements)

        return PagedContestsResponse.from(responsePage)
    }
}
