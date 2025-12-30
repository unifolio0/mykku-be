package com.example.mykku.contest.dto

data class ContestWinnersListResponse(
    val contests: List<ContestWinnerPreview>
) {
    data class ContestWinnerPreview(
        val contestId: Long,
        val contestTitle: String,
        val winners: List<WinnerThumbnail>
    )

    data class WinnerThumbnail(
        val winnerId: Long,
        val winnerRank: Int,
        val feedImageUrl: String?
    )
}
