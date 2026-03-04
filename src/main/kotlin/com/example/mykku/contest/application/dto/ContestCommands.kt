package com.example.mykku.contest.application.dto

import com.example.mykku.contest.domain.vo.ContestSortType
import com.example.mykku.contest.domain.vo.ContestStatusType
import java.time.LocalDateTime

data class CreateContestCommand(
    val title: String,
    val description: String?,
    val startedAt: LocalDateTime,
    val expiredAt: LocalDateTime,
    val thumbnailUrl: String,
    val images: List<ContestImageCommand>,
    val tags: List<String>
)

data class ContestImageCommand(
    val url: String,
    val orderIndex: Int
)

data class ContestListQuery(
    val status: ContestStatusType,
    val sortType: ContestSortType,
    val page: Int,
    val size: Int,
    val memberId: Long
)

data class SetContestWinnersCommand(
    val contestId: Long,
    val winners: List<WinnerSelectionCommand>
)

data class WinnerSelectionCommand(
    val participationId: Long,
    val winnerRank: Int,
    val description: String
)

data class UpdateAcceptanceSpeechCommand(
    val winnerId: Long,
    val memberId: Long?,
    val acceptanceSpeech: String
)

data class GetMyWinnerStatusQuery(
    val contestId: Long,
    val memberId: Long
)
