package com.example.mykku.contest.dto

import com.example.mykku.contest.domain.Contest
import com.example.mykku.contest.domain.ContestWinner

data class ContestWinnersResponse(
    val contestId: Long,
    val contestTitle: String,
    val winners: List<ContestWinnerResponse>
) {
    companion object {
        fun from(contest: Contest, winners: List<ContestWinner>): ContestWinnersResponse {
            return ContestWinnersResponse(
                contestId = contest.id!!,
                contestTitle = contest.title,
                winners = winners.map { ContestWinnerResponse.from(it) }
            )
        }
    }
}
