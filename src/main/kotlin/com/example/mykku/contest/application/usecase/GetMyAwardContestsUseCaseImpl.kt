package com.example.mykku.contest.application.usecase

import com.example.mykku.contest.application.dto.GetMyAwardsQuery
import com.example.mykku.contest.application.dto.MyAwardContestResult
import com.example.mykku.contest.application.dto.PagedMyAwardsResult
import com.example.mykku.contest.application.port.input.GetMyAwardContestsUseCase
import com.example.mykku.contest.application.port.output.ContestParticipationRepository
import com.example.mykku.contest.application.port.output.ContestRepository
import com.example.mykku.contest.application.port.output.ContestWinnerRepository
import com.example.mykku.feed.application.port.output.FeedImageRepository
import com.example.mykku.feed.application.port.output.FeedRepository
import com.example.mykku.feed.domain.vo.FeedId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetMyAwardContestsUseCaseImpl(
    private val contestWinnerRepository: ContestWinnerRepository,
    private val contestRepository: ContestRepository,
    private val contestParticipationRepository: ContestParticipationRepository,
    private val feedRepository: FeedRepository,
    private val feedImageRepository: FeedImageRepository
) : GetMyAwardContestsUseCase {

    @Transactional(readOnly = true)
    override fun execute(query: GetMyAwardsQuery): PagedMyAwardsResult {
        val winnerPage = contestWinnerRepository.findByMemberId(query.memberId, query.pageable)

        val contestIds = winnerPage.content.map { it.contestId }.distinct()
        val contestsMap = contestRepository.findAllByIds(contestIds)
            .associateBy { it.id.value }

        val participationIds = winnerPage.content.map { it.participationId }
        val participationsMap = contestParticipationRepository.findAllByIdIn(participationIds)
            .associateBy { it.id.value }

        val feedIds = participationsMap.values.map { FeedId(it.feedId) }
        val feedsMap = feedRepository.findAllByIds(feedIds)
            .associateBy { it.id!!.value }

        val feedImagesMap = feedImageRepository.findByFeedIds(feedIds)
            .groupBy { it.feedId.value }

        val results = winnerPage.content.map { winner ->
            val contest = contestsMap[winner.contestId.value]
            val participation = participationsMap[winner.participationId.value]
            val feed = participation?.let { feedsMap[it.feedId] }
            val feedImage = feed?.id?.value?.let { feedImagesMap[it]?.firstOrNull() }

            MyAwardContestResult(
                contestId = winner.contestId.value,
                contestTitle = contest?.title ?: "",
                thumbnailUrl = contest?.thumbnailUrl ?: "",
                winnerRank = winner.winnerRank,
                awardTitle = winner.awardTitle,
                acceptanceSpeech = winner.acceptanceSpeech,
                feedId = participation?.feedId ?: 0L,
                feedTitle = feed?.title ?: "",
                feedImageUrl = feedImage?.url
            )
        }

        return PagedMyAwardsResult(
            content = results,
            page = winnerPage.number,
            size = winnerPage.size,
            totalElements = winnerPage.totalElements,
            totalPages = winnerPage.totalPages,
            isLast = winnerPage.isLast
        )
    }
}
