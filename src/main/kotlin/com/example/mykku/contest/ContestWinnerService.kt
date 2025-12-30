package com.example.mykku.contest

import com.example.mykku.contest.domain.ContestStatusType
import com.example.mykku.contest.domain.ContestWinner
import com.example.mykku.contest.dto.ContestWinnerDetailResponse
import com.example.mykku.contest.dto.ContestWinnersListResponse
import com.example.mykku.contest.dto.SetContestWinnersRequest
import com.example.mykku.contest.dto.SetContestWinnersResponse
import com.example.mykku.contest.dto.UpdateAcceptanceSpeechRequest
import com.example.mykku.contest.dto.UpdateAcceptanceSpeechResponse
import com.example.mykku.contest.exception.ContestException
import com.example.mykku.contest.tool.ContestParticipationReader
import com.example.mykku.contest.tool.ContestReader
import com.example.mykku.contest.tool.ContestWinnerReader
import com.example.mykku.contest.tool.ContestWinnerWriter
import com.example.mykku.contest.tool.ContestWriter
import com.example.mykku.feed.domain.Feed
import com.example.mykku.feed.tool.FeedReader
import com.example.mykku.member.domain.Member
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ContestWinnerService(
    private val contestReader: ContestReader,
    private val contestWriter: ContestWriter,
    private val contestWinnerReader: ContestWinnerReader,
    private val contestWinnerWriter: ContestWinnerWriter,
    private val contestParticipationReader: ContestParticipationReader,
    private val feedReader: FeedReader
) {
    @Transactional
    fun setWinners(contestId: Long, request: SetContestWinnersRequest): SetContestWinnersResponse {
        val contest = contestReader.getContestById(contestId)
        validateContestForWinnerSelection(contest.status)
        validateWinnerRanks(request.winners)

        val participationsMap = fetchAndValidateParticipations(contestId, request.winners)

        contestWinnerWriter.deleteWinnersByContest(contest)

        val winnersData = request.winners.map { selection ->
            ContestWinnerWriter.WinnerData(
                rank = selection.winnerRank,
                description = selection.description,
                participation = participationsMap[selection.participationId]!!
            )
        }
        val savedWinners = contestWinnerWriter.createWinners(contest, winnersData)

        contestWriter.updateContestStatus(contest, ContestStatusType.WINNER_SELECTED)

        return buildSetContestWinnersResponse(contest.id!!, contest.title, savedWinners)
    }

    private fun validateContestForWinnerSelection(status: ContestStatusType) {
        if (status != ContestStatusType.EXPIRED && status != ContestStatusType.WINNER_SELECTING) {
            throw ContestException.contestNotExpired()
        }
    }

    private fun validateWinnerRanks(winners: List<SetContestWinnersRequest.WinnerSelection>) {
        val ranks = winners.map { it.winnerRank }
        if (ranks.any { it !in 1..3 }) {
            throw ContestException.invalidWinnerRank()
        }
        if (ranks.size != ranks.toSet().size) {
            throw ContestException.duplicateWinnerRank()
        }
    }

    private fun fetchAndValidateParticipations(
        contestId: Long,
        winners: List<SetContestWinnersRequest.WinnerSelection>
    ): Map<Long, com.example.mykku.contest.domain.ContestParticipation> {
        val participationIds = winners.map { it.participationId }
        val participationsMap = contestParticipationReader.getParticipationsByIds(participationIds)

        participationsMap.values.forEach { participation ->
            if (participation.contest.id != contestId) {
                throw ContestException.participationNotBelongToContest()
            }
        }

        return participationsMap
    }

    private fun buildSetContestWinnersResponse(
        contestId: Long,
        contestTitle: String,
        winners: List<ContestWinner>
    ): SetContestWinnersResponse {
        return SetContestWinnersResponse(
            contestId = contestId,
            contestTitle = contestTitle,
            winners = winners.map { winner ->
                SetContestWinnersResponse.WinnerInfo(
                    winnerId = winner.id!!,
                    winnerRank = winner.winnerRank,
                    feedId = winner.participation.feed.id!!,
                    feedTitle = winner.participation.feed.title,
                    authorNickname = winner.participation.member.nickname
                )
            }
        )
    }

    @Transactional(readOnly = true)
    fun getContestsWithWinners(): ContestWinnersListResponse {
        val contests = contestReader.getContestsByStatus(ContestStatusType.WINNER_SELECTED)
        if (contests.isEmpty()) {
            return ContestWinnersListResponse(emptyList())
        }

        val winnersByContest = contestWinnerReader.getWinnersByContests(contests)
        val allFeeds = extractFeedsFromWinners(winnersByContest)
        val feedImagesMap = feedReader.getFeedImagesByFeeds(allFeeds)

        val contestPreviews = contests.mapNotNull { contest ->
            val winners = winnersByContest[contest.id] ?: return@mapNotNull null
            ContestWinnersListResponse.ContestWinnerPreview(
                contestId = contest.id!!,
                contestTitle = contest.title,
                winners = winners.sortedBy { it.winnerRank }.map { winner ->
                    val feedImages = feedImagesMap[winner.participation.feed.id] ?: emptyList()
                    ContestWinnersListResponse.WinnerThumbnail(
                        winnerId = winner.id!!,
                        winnerRank = winner.winnerRank,
                        feedImageUrl = feedImages.firstOrNull()?.url
                    )
                }
            )
        }

        return ContestWinnersListResponse(contestPreviews)
    }

    private fun extractFeedsFromWinners(
        winnersByContest: Map<Long, List<ContestWinner>>
    ): List<Feed> {
        return winnersByContest.values.flatten().map { it.participation.feed }
    }

    @Transactional(readOnly = true)
    fun getContestWinnerDetail(contestId: Long): ContestWinnerDetailResponse {
        val contest = contestReader.getContestById(contestId)
        val winners = contestWinnerReader.getWinnersByContest(contest)

        if (winners.isEmpty()) {
            return ContestWinnerDetailResponse(
                contestId = contest.id!!,
                contestTitle = contest.title,
                winners = emptyList()
            )
        }

        val feeds = winners.map { it.participation.feed }
        val feedImagesMap = feedReader.getFeedImagesByFeeds(feeds)

        return ContestWinnerDetailResponse(
            contestId = contest.id!!,
            contestTitle = contest.title,
            winners = winners.sortedBy { it.winnerRank }.map { winner ->
                val feedImages = feedImagesMap[winner.participation.feed.id] ?: emptyList()
                ContestWinnerDetailResponse.WinnerDetail(
                    winnerId = winner.id!!,
                    winnerRank = winner.winnerRank,
                    feedId = winner.participation.feed.id!!,
                    feedTitle = winner.participation.feed.title,
                    feedImageUrl = feedImages.firstOrNull()?.url,
                    authorNickname = winner.participation.member.nickname,
                    authorProfileImage = winner.participation.member.profileImage,
                    description = winner.description,
                    acceptanceSpeech = winner.acceptanceSpeech
                )
            }
        )
    }

    @Transactional
    fun updateAcceptanceSpeech(
        winnerId: Long,
        member: Member,
        request: UpdateAcceptanceSpeechRequest
    ): UpdateAcceptanceSpeechResponse {
        val winner = contestWinnerReader.getWinnerByIdAndMember(winnerId, member)
        val updatedWinner = contestWinnerWriter.updateAcceptanceSpeech(winner, request.acceptanceSpeech)

        return UpdateAcceptanceSpeechResponse(
            winnerId = updatedWinner.id!!,
            acceptanceSpeech = updatedWinner.acceptanceSpeech
        )
    }
}
