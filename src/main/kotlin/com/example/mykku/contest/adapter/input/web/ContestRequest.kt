package com.example.mykku.contest.adapter.input.web

import com.example.mykku.contest.application.dto.SetContestWinnersCommand
import com.example.mykku.contest.application.dto.UpdateAcceptanceSpeechCommand
import com.example.mykku.contest.application.dto.WinnerSelectionCommand
import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size

data class SetContestWinnersRequest(
    @field:NotEmpty(message = "수상자 목록은 필수입니다")
    @field:Size(min = 1, max = 3, message = "수상자는 1~3명이어야 합니다")
    @field:Valid
    val winners: List<WinnerSelectionRequest>
) {
    fun toCommand(contestId: Long): SetContestWinnersCommand {
        return SetContestWinnersCommand(
            contestId = contestId,
            winners = winners.map {
                WinnerSelectionCommand(
                    participationId = it.participationId,
                    winnerRank = it.winnerRank,
                    description = it.description
                )
            }
        )
    }
}

data class WinnerSelectionRequest(
    val participationId: Long,

    @field:Min(value = 1, message = "순위는 1 이상이어야 합니다")
    @field:Max(value = 3, message = "순위는 3 이하여야 합니다")
    val winnerRank: Int,

    val description: String = ""
)

data class UpdateAcceptanceSpeechRequest(
    @field:NotBlank(message = "수상 소감은 필수입니다")
    @field:Size(max = 1000, message = "수상 소감은 1000자 이하여야 합니다")
    val acceptanceSpeech: String
) {
    fun toCommand(winnerId: Long, memberId: String?): UpdateAcceptanceSpeechCommand {
        return UpdateAcceptanceSpeechCommand(
            winnerId = winnerId,
            memberId = memberId,
            acceptanceSpeech = acceptanceSpeech
        )
    }
}
