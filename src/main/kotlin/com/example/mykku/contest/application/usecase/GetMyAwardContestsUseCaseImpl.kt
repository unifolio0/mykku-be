package com.example.mykku.contest.application.usecase

import com.example.mykku.contest.application.dto.GetMyAwardsQuery
import com.example.mykku.contest.application.dto.MyAwardContestResult
import com.example.mykku.contest.application.dto.PagedMyAwardsResult
import com.example.mykku.contest.application.port.input.GetMyAwardContestsUseCase
import com.example.mykku.contest.application.port.output.ContestRepository
import com.example.mykku.contest.application.port.output.ContestWinnerRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetMyAwardContestsUseCaseImpl(
    private val contestWinnerRepository: ContestWinnerRepository,
    private val contestRepository: ContestRepository
) : GetMyAwardContestsUseCase {

    @Transactional(readOnly = true)
    override fun execute(query: GetMyAwardsQuery): PagedMyAwardsResult {
        val winnerPage = contestWinnerRepository.findByMemberId(query.memberId, query.pageable)

        val contestIds = winnerPage.content.map { it.contestId }.distinct()
        val contestsMap = contestRepository.findAllByIds(contestIds)
            .associateBy { it.id.value }

        val results = winnerPage.content.map { winner ->
            val contest = contestsMap[winner.contestId.value]
            MyAwardContestResult(
                contestId = winner.contestId.value,
                contestTitle = contest?.title ?: "",
                thumbnailUrl = contest?.thumbnailUrl ?: "",
                winnerRank = winner.winnerRank,
                acceptanceSpeech = winner.acceptanceSpeech
            )
        }

        return PagedMyAwardsResult(
            content = results,
            page = winnerPage.number,
            size = winnerPage.size,
            totalElements = winnerPage.totalElements,
            totalPages = winnerPage.totalPages,
            isLast = winnerPage.isLast
        )
    }
}
