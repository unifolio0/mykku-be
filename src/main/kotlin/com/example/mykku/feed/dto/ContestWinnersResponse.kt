package com.example.mykku.feed.dto

import com.example.mykku.feed.domain.Contest

data class ContestWinnersResponse(
    val title: String,
    val winners: List<ContestWinnerResponse>,
) {
    constructor(contest: Contest) : this(
        title = contest.title,
        winners = contest.contestWinners
            .map { winner -> ContestWinnerResponse(winner) }
            .sortedBy { it.rank }
    )
}
