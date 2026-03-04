package com.example.mykku.contest.application.usecase

import com.example.mykku.contest.application.dto.GetMyAwardsPreviewQuery
import com.example.mykku.contest.application.dto.MyAwardPreviewResult
import com.example.mykku.contest.application.port.input.GetMyAwardsPreviewUseCase
import com.example.mykku.contest.application.port.output.ContestRepository
import com.example.mykku.contest.application.port.output.ContestWinnerRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetMyAwardsPreviewUseCaseImpl(
    private val contestWinnerRepository: ContestWinnerRepository,
    private val contestRepository: ContestRepository
) : GetMyAwardsPreviewUseCase {

    @Transactional(readOnly = true)
    override fun execute(query: GetMyAwardsPreviewQuery): List<MyAwardPreviewResult> {
        val winners = contestWinnerRepository.findByMemberId(query.memberId).take(PREVIEW_LIMIT)
        if (winners.isEmpty()) return emptyList()

        val contestIds = winners.map { it.contestId }.distinct()
        val contestsMap = contestRepository.findAllByIds(contestIds)
            .associateBy { it.id.value }

        return winners.mapNotNull { winner ->
            val contest = contestsMap[winner.contestId.value] ?: return@mapNotNull null
            MyAwardPreviewResult(
                contestId = contest.id.value,
                thumbnailUrl = contest.thumbnailUrl
            )
        }
    }

    companion object {
        private const val PREVIEW_LIMIT = 3
    }
}
