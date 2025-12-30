package com.example.mykku.contest.tool

import com.example.mykku.contest.domain.Contest
import com.example.mykku.contest.domain.ContestParticipation
import com.example.mykku.contest.domain.ContestWinner
import com.example.mykku.contest.repository.ContestWinnerRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class ContestWinnerWriter(
    private val contestWinnerRepository: ContestWinnerRepository
) {
    @Transactional
    fun createWinners(
        contest: Contest,
        winnersData: List<WinnerData>
    ): List<ContestWinner> {
        val winners = winnersData.map { data ->
            ContestWinner(
                winnerRank = data.rank,
                description = data.description,
                contest = contest,
                participation = data.participation
            )
        }
        return contestWinnerRepository.saveAll(winners)
    }

    @Transactional
    fun deleteWinnersByContest(contest: Contest) {
        contestWinnerRepository.deleteAllByContest(contest)
    }

    @Transactional
    fun updateAcceptanceSpeech(winner: ContestWinner, speech: String): ContestWinner {
        winner.updateAcceptanceSpeech(speech)
        return contestWinnerRepository.save(winner)
    }


    @Transactional
    fun deleteAllByParticipations(participations: List<ContestParticipation>) {
        if (participations.isNotEmpty()) {
            contestWinnerRepository.deleteAllByParticipationIn(participations)
        }
    }

    data class WinnerData(
        val rank: Int,
        val description: String,
        val participation: ContestParticipation
    )
}
