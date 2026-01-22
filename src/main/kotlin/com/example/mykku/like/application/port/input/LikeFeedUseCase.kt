package com.example.mykku.like.application.port.input

import com.example.mykku.like.application.dto.LikeFeedCommand
import com.example.mykku.like.application.dto.LikeFeedResult
import com.example.mykku.like.application.dto.UnlikeFeedCommand

interface LikeFeedUseCase {
    fun likeFeed(command: LikeFeedCommand): LikeFeedResult
    fun unlikeFeed(command: UnlikeFeedCommand)
    fun isLiked(memberId: String, feedId: Long): Boolean
    fun getLikedFeedIds(memberId: String, feedIds: List<Long>): Set<Long>
    fun deleteAllByFeedId(feedId: Long)
}
