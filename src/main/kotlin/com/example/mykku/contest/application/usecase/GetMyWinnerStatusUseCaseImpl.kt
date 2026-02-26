package com.example.mykku.contest.application.usecase

import com.example.mykku.contest.application.dto.GetMyWinnerStatusQuery
import com.example.mykku.contest.application.dto.MyWinnerStatusResult
import com.example.mykku.contest.application.port.input.GetMyWinnerStatusUseCase
import com.example.mykku.contest.application.port.output.ContestRepository
import com.example.mykku.contest.application.port.output.ContestWinnerRepository
import com.example.mykku.contest.domain.vo.ContestId
import com.example.mykku.contest.domain.vo.ContestStatusType
import com.example.mykku.contest.exception.ContestException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetMyWinnerStatusUseCaseImpl(
    private val contestRepository: ContestRepository,
    private val contestWinnerRepository: ContestWinnerRepository
) : GetMyWinnerStatusUseCase {

    @Transactional(readOnly = true)
    override fun execute(query: GetMyWinnerStatusQuery): MyWinnerStatusResult {
        val contestId = ContestId.of(query.contestId)
        val contest = contestRepository.findById(contestId)
            ?: throw ContestException.contestNotFound()

        if (contest.status != ContestStatusType.WINNER_SELECTED) {
            throw ContestException.winnerNotAnnounced()
        }

        val myWinner = contestWinnerRepository.findByContestIdAndMemberId(contestId, query.memberId)
            ?: return notWinnerResult()

        return MyWinnerStatusResult(
            isWinner = true,
            winnerId = myWinner.id.value,
            winnerRank = myWinner.winnerRank
        )
    }

    private fun notWinnerResult() = MyWinnerStatusResult(
        isWinner = false,
        winnerId = null,
        winnerRank = null
    )
}
