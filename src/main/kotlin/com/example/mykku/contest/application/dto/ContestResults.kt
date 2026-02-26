package com.example.mykku.contest.application.dto

import com.example.mykku.contest.domain.vo.ContestStatusType
import java.time.LocalDateTime

data class CreateContestResult(
    val id: Long,
    val title: String,
    val description: String?,
    val startedAt: LocalDateTime,
    val expiredAt: LocalDateTime,
    val thumbnailUrl: String,
    val images: List<ContestImageResult>,
    val tags: List<String>,
    val createdAt: LocalDateTime
)

data class ContestImageResult(
    val url: String,
    val orderIndex: Int
)

data class ContestListResult(
    val id: Long,
    val title: String,
    val startedAt: LocalDateTime,
    val expiredAt: LocalDateTime,
    val status: ContestStatusType,
    val thumbnailUrl: String,
    val tags: List<String>,
    val isSaved: Boolean
)

data class PagedContestsResult(
    val content: List<ContestListResult>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
    val isLast: Boolean
)

data class ContestDetailResult(
    val id: Long,
    val title: String,
    val description: String?,
    val startedAt: LocalDateTime,
    val expiredAt: LocalDateTime,
    val status: ContestStatusType,
    val thumbnailUrl: String,
    val images: List<ContestImageResult>,
    val tags: List<String>,
    val isSaved: Boolean,
    val createdAt: LocalDateTime
)

data class ContestPreviewResult(
    val id: Long,
    val title: String,
    val thumbnailUrl: String
)

data class SetContestWinnersResult(
    val contestId: Long,
    val contestTitle: String,
    val winners: List<WinnerInfoResult>
)

data class WinnerInfoResult(
    val winnerId: Long,
    val winnerRank: Int,
    val feedId: Long,
    val feedTitle: String,
    val authorNickname: String
)

data class ContestWinnersListResult(
    val contests: List<ContestWinnerPreviewResult>
)

data class ContestWinnerPreviewResult(
    val contestId: Long,
    val contestTitle: String,
    val winners: List<WinnerThumbnailResult>
)

data class WinnerThumbnailResult(
    val winnerId: Long,
    val winnerRank: Int,
    val feedImageUrl: String?
)

data class ContestWinnerDetailResult(
    val contestId: Long,
    val contestTitle: String,
    val winners: List<WinnerDetailResult>
)

data class WinnerDetailResult(
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

data class UpdateAcceptanceSpeechResult(
    val winnerId: Long,
    val acceptanceSpeech: String
)

data class MyWinnerStatusResult(
    val isWinner: Boolean,
    val winnerId: Long?,
    val winnerRank: Int?
)
