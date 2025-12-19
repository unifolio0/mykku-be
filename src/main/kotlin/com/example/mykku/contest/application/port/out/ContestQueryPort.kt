package com.example.mykku.contest.application.port.out

import com.example.mykku.contest.domain.Contest
import com.example.mykku.contest.domain.ContestImage
import com.example.mykku.contest.domain.ContestSortType
import com.example.mykku.contest.domain.ContestStatusType
import com.example.mykku.contest.domain.ContestTag
import com.example.mykku.contest.domain.model.ContestId
import com.example.mykku.contest.dto.ContestPreviewResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.LocalDateTime

data class ContestSummary(
    val id: Long,
    val title: String,
    val startedAt: LocalDateTime,
    val expiredAt: LocalDateTime
)

interface ContestQueryPort {
    fun findSummaryById(id: ContestId): ContestSummary?
    fun existsById(id: ContestId): Boolean

    // Cross-domain methods (for internal use)
    fun getContestById(contestId: Long): Contest
    fun getProcessingContestPreviews(): List<ContestPreviewResponse>
    fun getContestsWithPagination(
        status: ContestStatusType,
        sortType: ContestSortType,
        pageable: Pageable,
        currentTime: LocalDateTime
    ): Page<Contest>
    fun getContestByIdWithRelations(contestId: Long): Contest
    fun getContestImages(contests: List<Contest>): Map<Long, List<ContestImage>>
    fun getContestTags(contests: List<Contest>): Map<Long, List<ContestTag>>
    fun getContestTagsByTitles(titles: List<String>): List<ContestTag>
    fun getActiveContestsWithAllTags(): List<Pair<Contest, Set<String>>>
}
