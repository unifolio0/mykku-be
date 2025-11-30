package com.example.mykku.contest.dto

import com.example.mykku.contest.domain.Contest

data class ContestWinnersResponse(
    val title: String,
    val winners: List<ContestWinnerResponse>,
) {
    constructor(contest: Contest) : this(
        title = contest.title,
        winners = mutableListOf()
    )
}
