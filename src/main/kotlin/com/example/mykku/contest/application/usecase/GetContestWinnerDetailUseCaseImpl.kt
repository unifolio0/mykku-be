package com.example.mykku.contest.application.usecase

import com.example.mykku.contest.application.dto.ContestWinnerDetailResult
import com.example.mykku.contest.application.dto.WinnerDetailResult
import com.example.mykku.contest.application.port.input.GetContestWinnerDetailUseCase
import com.example.mykku.contest.application.port.output.ContestParticipationRepository
import com.example.mykku.contest.application.port.output.ContestRepository
import com.example.mykku.contest.application.port.output.ContestWinnerRepository
import com.example.mykku.contest.domain.vo.ContestId
import com.example.mykku.contest.exception.ContestException
import com.example.mykku.feed.application.port.output.FeedImageRepository
import com.example.mykku.feed.application.port.output.FeedRepository
import com.example.mykku.member.application.port.output.MemberRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetContestWinnerDetailUseCaseImpl(
    private val contestRepository: ContestRepository,
    private val contestWinnerRepository: ContestWinnerRepository,
    private val contestParticipationRepository: ContestParticipationRepository,
    private val feedRepository: FeedRepository,
    private val feedImageRepository: FeedImageRepository,
    private val memberRepository: MemberRepository
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

        val winnerDetails = winners.sortedBy { it.winnerRank }.mapNotNull { winner ->
            val participation = participationsMap[winner.participationId.value] ?: return@mapNotNull null

            WinnerDetailResult(
                winnerId = winner.id.value,
                winnerRank = winner.winnerRank,
                feedId = participation.feedId,
                feedTitle = "",
                feedImageUrl = null,
                authorNickname = "",
                authorProfileImage = "",
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
