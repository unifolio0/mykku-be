package com.example.mykku.contest.application.usecase

import com.example.mykku.contest.application.dto.SetContestWinnersCommand
import com.example.mykku.contest.application.dto.SetContestWinnersResult
import com.example.mykku.contest.application.dto.WinnerInfoResult
import com.example.mykku.contest.application.port.input.SetContestWinnersUseCase
import com.example.mykku.contest.application.port.output.ContestParticipationRepository
import com.example.mykku.contest.application.port.output.ContestRepository
import com.example.mykku.contest.application.port.output.ContestWinnerRepository
import com.example.mykku.contest.domain.entity.ContestWinner
import com.example.mykku.contest.domain.vo.ContestId
import com.example.mykku.contest.domain.vo.ContestParticipationId
import com.example.mykku.contest.domain.vo.ContestStatusType
import com.example.mykku.contest.exception.ContestException
import com.example.mykku.feed.tool.FeedReader
import com.example.mykku.member.tool.MemberReader
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SetContestWinnersUseCaseImpl(
    private val contestRepository: ContestRepository,
    private val contestParticipationRepository: ContestParticipationRepository,
    private val contestWinnerRepository: ContestWinnerRepository,
    private val feedReader: FeedReader,
    private val memberReader: MemberReader
) : SetContestWinnersUseCase {

    @Transactional
    override fun execute(command: SetContestWinnersCommand): SetContestWinnersResult {
        val contestId = ContestId.of(command.contestId)
        val contest = contestRepository.findById(contestId)
            ?: throw ContestException.contestNotFound()

        validateContestForWinnerSelection(contest.status)
        validateWinnerRanks(command)

        val participationsMap = fetchAndValidateParticipations(contestId, command)

        contestWinnerRepository.deleteAllByContestId(contestId)

        val winners = command.winners.map { selection ->
            ContestWinner.create(
                winnerRank = selection.winnerRank,
                description = selection.description,
                contestId = contestId,
                participationId = ContestParticipationId.of(selection.participationId)
            )
        }
        val savedWinners = contestWinnerRepository.saveAll(winners)

        contest.updateStatus(ContestStatusType.WINNER_SELECTED)
        contestRepository.save(contest)

        return buildResult(contest, savedWinners, participationsMap)
    }

    private fun validateContestForWinnerSelection(status: ContestStatusType) {
        if (status != ContestStatusType.EXPIRED && status != ContestStatusType.WINNER_SELECTING) {
            throw ContestException.contestNotExpired()
        }
    }

    private fun validateWinnerRanks(command: SetContestWinnersCommand) {
        val ranks = command.winners.map { it.winnerRank }
        if (ranks.any { it !in 1..3 }) {
            throw ContestException.invalidWinnerRank()
        }
        if (ranks.size != ranks.toSet().size) {
            throw ContestException.duplicateWinnerRank()
        }
    }

    private fun fetchAndValidateParticipations(
        contestId: ContestId,
        command: SetContestWinnersCommand
    ): Map<Long, com.example.mykku.contest.domain.entity.ContestParticipation> {
        val participationIds = command.winners.map { ContestParticipationId.of(it.participationId) }
        val participations = contestParticipationRepository.findAllByIdIn(participationIds)

        if (participations.size != participationIds.size) {
            throw ContestException.participationNotFound()
        }

        participations.forEach { participation ->
            if (participation.contestId != contestId) {
                throw ContestException.participationNotBelongToContest()
            }
        }

        return participations.associateBy { it.id.value }
    }

    private fun buildResult(
        contest: com.example.mykku.contest.domain.entity.Contest,
        winners: List<ContestWinner>,
        participationsMap: Map<Long, com.example.mykku.contest.domain.entity.ContestParticipation>
    ): SetContestWinnersResult {
        val winnerInfos = winners.map { winner ->
            val participation = participationsMap[winner.participationId.value]!!
            val feed = feedReader.findById(participation.feedId)
            val member = memberReader.getMemberById(participation.memberId)

            WinnerInfoResult(
                winnerId = winner.id.value,
                winnerRank = winner.winnerRank,
                feedId = feed.id!!,
                feedTitle = feed.title,
                authorNickname = member.nickname
            )
        }

        return SetContestWinnersResult(
            contestId = contest.id.value,
            contestTitle = contest.title,
            winners = winnerInfos
        )
    }
}
