package com.example.mykku.contest.application.usecase

import com.example.mykku.contest.application.dto.ContestWinnerPreviewResult
import com.example.mykku.contest.application.dto.ContestWinnersListResult
import com.example.mykku.contest.application.dto.WinnerThumbnailResult
import com.example.mykku.contest.application.port.input.GetContestWinnersListUseCase
import com.example.mykku.contest.application.port.output.ContestParticipationRepository
import com.example.mykku.contest.application.port.output.ContestRepository
import com.example.mykku.contest.application.port.output.ContestWinnerRepository
import com.example.mykku.contest.domain.vo.ContestParticipationId
import com.example.mykku.contest.domain.vo.ContestStatusType
import com.example.mykku.feed.tool.FeedReader
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetContestWinnersListUseCaseImpl(
    private val contestRepository: ContestRepository,
    private val contestWinnerRepository: ContestWinnerRepository,
    private val contestParticipationRepository: ContestParticipationRepository,
    private val feedReader: FeedReader
) : GetContestWinnersListUseCase {

    @Transactional(readOnly = true)
    override fun execute(): ContestWinnersListResult {
        val contests = contestRepository.findByStatus(ContestStatusType.WINNER_SELECTED)

        if (contests.isEmpty()) {
            return ContestWinnersListResult(emptyList())
        }

        val contestIds = contests.map { it.id }
        val winnersByContestId = contestWinnerRepository.findByContestIds(contestIds)
            .groupBy { it.contestId.value }

        val allWinners = winnersByContestId.values.flatten()
        val participationIds = allWinners.map { it.participationId }
        val participations = contestParticipationRepository.findAllByIdIn(participationIds)
        val participationsMap = participations.associateBy { it.id.value }

        val feedIds = participations.map { it.feedId }
        val feedImagesMap = feedReader.getFeedImagesByFeedIds(feedIds)

        val contestPreviews = contests.mapNotNull { contest ->
            val winners = winnersByContestId[contest.id.value] ?: return@mapNotNull null

            ContestWinnerPreviewResult(
                contestId = contest.id.value,
                contestTitle = contest.title,
                winners = winners.sortedBy { it.winnerRank }.map { winner ->
                    val participation = participationsMap[winner.participationId.value]
                    val feedImages = participation?.let { feedImagesMap[it.feedId] } ?: emptyList()
                    WinnerThumbnailResult(
                        winnerId = winner.id.value,
                        winnerRank = winner.winnerRank,
                        feedImageUrl = feedImages.firstOrNull()?.url
                    )
                }
            )
        }

        return ContestWinnersListResult(contestPreviews)
    }
}
