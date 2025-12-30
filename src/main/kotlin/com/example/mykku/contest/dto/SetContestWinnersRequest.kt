package com.example.mykku.contest.dto

import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size

data class SetContestWinnersRequest(
    @field:NotEmpty(message = "수상자 목록은 필수입니다")
    @field:Size(min = 1, max = 3, message = "수상자는 1~3명이어야 합니다")
    @field:Valid
    val winners: List<WinnerSelection>
) {
    data class WinnerSelection(
        val participationId: Long,

        @field:Min(value = 1, message = "순위는 1 이상이어야 합니다")
        @field:Max(value = 3, message = "순위는 3 이하여야 합니다")
        val winnerRank: Int,

        val description: String = ""
    )
}
