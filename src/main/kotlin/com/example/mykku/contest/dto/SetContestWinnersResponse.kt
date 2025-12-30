package com.example.mykku.contest.dto

data class SetContestWinnersResponse(
    val contestId: Long,
    val contestTitle: String,
    val winners: List<WinnerInfo>
) {
    data class WinnerInfo(
        val winnerId: Long,
        val winnerRank: Int,
        val feedId: Long,
        val feedTitle: String,
        val authorNickname: String
    )
}
