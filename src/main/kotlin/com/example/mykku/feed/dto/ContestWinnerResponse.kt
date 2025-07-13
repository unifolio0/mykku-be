package com.example.mykku.feed.dto

import com.example.mykku.feed.domain.ContestWinner

data class ContestWinnerResponse(
    val id: Long,
    val image: String,
    val rank: Int,
) {
    constructor(winner: ContestWinner) : this(
        id = winner.id!!,
        image = winner.image,
        rank = winner.winnerRank
    )
}
