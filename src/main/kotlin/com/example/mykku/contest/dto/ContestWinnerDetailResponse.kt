package com.example.mykku.contest.dto

data class ContestWinnerDetailResponse(
    val contestId: Long,
    val contestTitle: String,
    val winners: List<WinnerDetail>
) {
    data class WinnerDetail(
        val winnerId: Long,
        val winnerRank: Int,
        val feedId: Long,
        val feedTitle: String,
        val feedImageUrl: String?,
        val authorNickname: String,
        val authorProfileImage: String?,
        val description: String,
        val acceptanceSpeech: String
    )
}
