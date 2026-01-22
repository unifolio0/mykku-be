package com.example.mykku.contest.application.usecase

import com.example.mykku.contest.application.dto.ContestWinnerDetailResult
import com.example.mykku.contest.application.dto.WinnerDetailResult
import com.example.mykku.contest.application.port.input.GetContestWinnerDetailUseCase
import com.example.mykku.contest.application.port.output.ContestParticipationRepository
import com.example.mykku.contest.application.port.output.ContestRepository
import com.example.mykku.contest.application.port.output.ContestWinnerRepository
import com.example.mykku.contest.domain.vo.ContestId
import com.example.mykku.contest.exception.ContestException
import com.example.mykku.feed.tool.FeedReader
import com.example.mykku.member.tool.MemberReader
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetContestWinnerDetailUseCaseImpl(
    private val contestRepository: ContestRepository,
    private val contestWinnerRepository: ContestWinnerRepository,
    private val contestParticipationRepository: ContestParticipationRepository,
    private val feedReader: FeedReader,
    private val memberReader: MemberReader
) : GetContestWinnerDetailUseCase {

    @Transactional(readOnly = true)
    override fun execute(contestId: Long): ContestWinnerDetailResult {
        val contest = contestRepository.findById(ContestId.of(contestId))
            ?: throw ContestException.contestNotFound()

        val winners = contestWinnerRepository.findByContestId(contest.id)

        if (winners.isEmpty()) {
            return ContestWinnerDetailResult(
                contestId = contest.id.value,
                contestTitle = contest.title,
                winners = emptyList()
            )
        }

        val participationIds = winners.map { it.participationId }
        val participations = contestParticipationRepository.findAllByIdIn(participationIds)
        val participationsMap = participations.associateBy { it.id.value }

        val feedIds = participations.map { it.feedId }
        val feeds = feedReader.findByIds(feedIds)
        val feedsMap = feeds.associateBy { it.id!! }
        val feedImagesMap = feedReader.getFeedImagesByFeedIds(feedIds)

        val memberIds = participations.map { it.memberId }
        val membersMap = memberReader.getMembersByIds(memberIds).associateBy { it.id!! }

        val winnerDetails = winners.sortedBy { it.winnerRank }.map { winner ->
            val participation = participationsMap[winner.participationId.value]!!
            val feed = feedsMap[participation.feedId]!!
            val feedImages = feedImagesMap[participation.feedId] ?: emptyList()
            val member = membersMap[participation.memberId]!!

            WinnerDetailResult(
                winnerId = winner.id.value,
                winnerRank = winner.winnerRank,
                feedId = feed.id!!,
                feedTitle = feed.title,
                feedImageUrl = feedImages.firstOrNull()?.url,
                authorNickname = member.nickname,
                authorProfileImage = member.profileImage,
                description = winner.description,
                acceptanceSpeech = winner.acceptanceSpeech
            )
        }

        return ContestWinnerDetailResult(
            contestId = contest.id.value,
            contestTitle = contest.title,
            winners = winnerDetails
        )
    }
}
