package com.example.mykku.feed.dto

import com.example.mykku.feed.domain.Event

data class ContestWinnersResponse(
    val title: String,
    val winners: List<ContestWinnerResponse>,
) {
    constructor(contest: Event) : this(
        title = contest.title,
        winners = mutableListOf()
    )
}
