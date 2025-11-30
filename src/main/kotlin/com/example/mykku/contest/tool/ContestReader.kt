package com.example.mykku.contest.tool

import com.example.mykku.contest.domain.Contest
import com.example.mykku.contest.domain.ContestImage
import com.example.mykku.contest.domain.ContestSortType
import com.example.mykku.contest.domain.ContestStatusType
import com.example.mykku.contest.domain.ContestTag
import com.example.mykku.contest.domain.ContestWinner
import com.example.mykku.contest.dto.ContestPreviewResponse
import com.example.mykku.contest.exception.ContestException
import com.example.mykku.contest.repository.ContestImageRepository
import com.example.mykku.contest.repository.ContestRepository
import com.example.mykku.contest.repository.ContestTagRepository
import com.example.mykku.contest.repository.ContestWinnerRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class ContestReader(
    private val contestRepository: ContestRepository,
    private val contestImageRepository: ContestImageRepository,
    private val contestTagRepository: ContestTagRepository,
    private val contestWinnerRepository: ContestWinnerRepository,
) {
    fun getContestById(contestId: Long): Contest {
        return contestRepository.findByIdOrNull(contestId)
            ?: throw ContestException.contestNotFound()
    }

    fun getProcessingContestPreviews(): List<ContestPreviewResponse> {
        val contests = contestRepository.findByExpiredAtAfter(LocalDateTime.now()).take(5)
        val contestImages = contestImageRepository.findByContestIn(contests)
        val imagesByContest = contestImages.groupBy { it.contest }

        return contests.map { contest ->
            ContestPreviewResponse(contest, imagesByContest[contest] ?: emptyList())
        }
    }

    fun getContestsWithPagination(
        status: ContestStatusType,
        sortType: ContestSortType,
        pageable: Pageable,
        currentTime: LocalDateTime
    ): Page<Contest> {
        return when (status) {
            ContestStatusType.ACTIVE -> getActiveContestsBySortType(sortType, currentTime, pageable)
            ContestStatusType.EXPIRED -> contestRepository.findByExpiredAtLessThanEqualOrderByCreatedAtDesc(currentTime, pageable)
            ContestStatusType.ALL -> contestRepository.findAllByOrderByCreatedAtDesc(pageable)
        }
    }

    private fun getActiveContestsBySortType(
        sortType: ContestSortType,
        currentTime: LocalDateTime,
        pageable: Pageable
    ): Page<Contest> {
        return when (sortType) {
            ContestSortType.LATEST -> contestRepository.findByExpiredAtAfterOrderByCreatedAtDesc(currentTime, pageable)
            ContestSortType.OLDEST -> contestRepository.findByExpiredAtAfterOrderByCreatedAtAsc(currentTime, pageable)
            ContestSortType.POPULAR -> contestRepository.findActiveContestsByPopular(currentTime, pageable)
        }
    }

    fun getContestByIdWithRelations(contestId: Long): Contest {
        return contestRepository.findByIdOrNull(contestId)
            ?: throw ContestException.contestNotFound()
    }

    fun getContestImages(contests: List<Contest>): Map<Long, List<ContestImage>> {
        val images = contestImageRepository.findByContestIn(contests)
        return images.groupBy { it.contest.id!! }
    }

    fun getContestImagesByContest(contest: Contest): List<ContestImage> {
        return contestImageRepository.findByContest(contest)
    }

    fun getContestTags(contests: List<Contest>): Map<Long, List<ContestTag>> {
        val tags = contestTagRepository.findByContestIn(contests)
        return tags.groupBy { it.contest.id!! }
    }

    fun getContestTagsByContest(contest: Contest): List<ContestTag> {
        return contestTagRepository.findByContest(contest)
    }

    fun getContestWinners(contests: List<Contest>): Map<Long, List<ContestWinner>> {
        val winners = contestWinnerRepository.findByContestIn(contests)
        return winners.groupBy { it.contest.id!! }
    }

    fun getContestWinnersByContest(contest: Contest): List<ContestWinner> {
        return contestWinnerRepository.findByContestOrderByWinnerRankAsc(contest)
    }
}
