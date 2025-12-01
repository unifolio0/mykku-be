package com.example.mykku.contest.dto

import com.example.mykku.contest.domain.ContestWinner

data class ContestWinnerResponse(
    val id: Long,
    val winnerRank: Int,
    val description: String,
    val acceptanceSpeech: String,
    val image: String
) {
    companion object {
        fun from(winner: ContestWinner): ContestWinnerResponse {
            return ContestWinnerResponse(
                id = winner.id!!,
                winnerRank = winner.winnerRank,
                description = winner.description,
                acceptanceSpeech = winner.acceptanceSpeech,
                image = winner.image
            )
        }
    }
}
