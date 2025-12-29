package com.example.mykku.contest.dto

import com.example.mykku.contest.domain.Contest
import com.example.mykku.contest.domain.ContestWinner
import com.example.mykku.feed.domain.FeedImage

data class ContestWinnersResponse(
    val contestId: Long,
    val contestTitle: String,
    val winners: List<ContestWinnerResponse>
) {
    companion object {
        fun from(
            contest: Contest,
            winners: List<ContestWinner>,
            feedImagesMap: Map<Long, List<FeedImage>>
        ): ContestWinnersResponse {
            return ContestWinnersResponse(
                contestId = contest.id!!,
                contestTitle = contest.title,
                winners = winners.map { winner ->
                    val feedImages = feedImagesMap[winner.participation.feed.id] ?: emptyList()
                    ContestWinnerResponse.from(winner, feedImages.firstOrNull())
                }
            )
        }
    }
}
