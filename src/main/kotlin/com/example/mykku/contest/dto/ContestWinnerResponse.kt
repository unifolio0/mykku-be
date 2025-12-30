package com.example.mykku.contest.dto

import com.example.mykku.contest.domain.ContestWinner
import com.example.mykku.feed.domain.FeedImage

data class ContestWinnerResponse(
    val id: Long,
    val winnerRank: Int,
    val description: String,
    val acceptanceSpeech: String,
    val feedId: Long,
    val feedTitle: String,
    val feedImageUrl: String?,
    val authorId: String,
    val authorNickname: String,
    val authorProfileImage: String?
) {
    companion object {
        fun from(winner: ContestWinner, feedImage: FeedImage?): ContestWinnerResponse {
            val feed = winner.participation.feed
            val member = winner.participation.member
            return ContestWinnerResponse(
                id = winner.id!!,
                winnerRank = winner.winnerRank,
                description = winner.description,
                acceptanceSpeech = winner.acceptanceSpeech,
                feedId = feed.id!!,
                feedTitle = feed.title,
                feedImageUrl = feedImage?.url,
                authorId = member.id,
                authorNickname = member.nickname,
                authorProfileImage = member.profileImage
            )
        }
    }
}
