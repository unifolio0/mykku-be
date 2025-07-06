package com.example.mykku.feed.dto

import com.example.mykku.feed.domain.Event

data class ContestWinnersResponse(
    val title: String,
    val winners: List<ContestWinnerResponse>,
) {
    constructor(event: Event) : this(
        title = event.title,
        winners = mutableListOf()
    )
}
