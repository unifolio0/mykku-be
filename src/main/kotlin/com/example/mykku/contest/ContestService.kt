package com.example.mykku.contest

import com.example.mykku.common.util.PageableValidator
import com.example.mykku.contest.domain.Contest
import com.example.mykku.contest.domain.ContestSortType
import com.example.mykku.contest.domain.ContestStatusType
import com.example.mykku.contest.dto.*
import com.example.mykku.contest.tool.ContestDtoConverter
import com.example.mykku.contest.tool.ContestParticipationReader
import com.example.mykku.contest.tool.ContestReader
import com.example.mykku.contest.tool.ContestWriter
import com.example.mykku.member.domain.Member
import com.example.mykku.scrap.tool.SaveContestReader
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class ContestService(
    private val contestWriter: ContestWriter,
    private val contestReader: ContestReader,
    private val contestParticipationReader: ContestParticipationReader,
    private val saveContestReader: SaveContestReader,
    private val contestDtoConverter: ContestDtoConverter
) {

    @Transactional
    fun createContest(request: CreateContestRequest): CreateContestResponse {
        val (contest, contestImages, contestTags) = contestWriter.createContest(
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
        val contestPage = contestReader.getContestsWithPagination(status, sortType, pageable, LocalDateTime.now())

        val contestListResponses = convertToContestListResponses(contestPage.content, member)
        val responseMap = contestListResponses.associateBy { it.id }
        val responsePage = contestPage.map { contest ->
            responseMap[contest.id]!!
        }

        return PagedContestsResponse.from(responsePage)
    }

    private fun convertToContestListResponses(contests: List<Contest>, member: Member): List<ContestListResponse> {
        val imagesByContestId = contestReader.getContestImages(contests)
        val tagsByContestId = contestReader.getContestTags(contests)
        val savedContestIds = saveContestReader.getSavedContestIds(member, contests)

        return contests.map { contest ->
            val images = imagesByContestId[contest.id!!] ?: emptyList()
            val tags = tagsByContestId[contest.id!!] ?: emptyList()
            val isSaved = savedContestIds.contains(contest.id)
            contestDtoConverter.toContestListResponse(contest, images, tags, isSaved)
        }
    }

    @Transactional(readOnly = true)
    fun getContestDetail(contestId: Long, member: Member): ContestDetailResponse {
        val contest = contestReader.getContestByIdWithRelations(contestId)
        val images = contestReader.getContestImages(listOf(contest))[contest.id] ?: emptyList()
        val tags = contestReader.getContestTags(listOf(contest))[contest.id] ?: emptyList()
        val isSaved = saveContestReader.isSaved(member, contest)

        return contestDtoConverter.toContestDetailResponse(contest, images, tags, isSaved)
    }

    @Transactional(readOnly = true)
    fun getMyParticipatedContests(member: Member, page: Int, size: Int): PagedContestsResponse {
        val pageable = PageableValidator.validateAndCreate(page, size)
        val contestPage = contestParticipationReader.getParticipatedContests(member, pageable)

        val contestListResponses = convertToContestListResponses(contestPage.content, member)
        val responseMap = contestListResponses.associateBy { it.id }
        val responsePage = contestPage.map { contest ->
            responseMap[contest.id]!!
        }

        return PagedContestsResponse.from(responsePage)
    }
}
