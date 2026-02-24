package com.example.mykku.contest.application.usecase

import com.example.mykku.contest.application.dto.GetMyWinnerStatusQuery
import com.example.mykku.contest.application.dto.MyWinnerStatusResult
import com.example.mykku.contest.application.port.input.GetMyWinnerStatusUseCase
import com.example.mykku.contest.application.port.output.ContestParticipationRepository
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
    private val contestWinnerRepository: ContestWinnerRepository,
    private val contestParticipationRepository: ContestParticipationRepository
) : GetMyWinnerStatusUseCase {

    @Transactional(readOnly = true)
    override fun execute(query: GetMyWinnerStatusQuery): MyWinnerStatusResult {
        val contestId = ContestId.of(query.contestId)
        val contest = contestRepository.findById(contestId)
            ?: throw ContestException.contestNotFound()

        if (contest.status != ContestStatusType.WINNER_SELECTED) {
            throw ContestException.winnerNotAnnounced()
        }

        val myParticipations = contestParticipationRepository.findByMemberIdAndContestIds(
            query.memberId, listOf(contestId)
        )
        if (myParticipations.isEmpty()) {
            return notWinnerResult()
        }

        val winners = contestWinnerRepository.findByContestId(contestId)
        val myParticipationIds = myParticipations.map { it.id }.toSet()
        val myWinner = winners.find { it.participationId in myParticipationIds }
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
