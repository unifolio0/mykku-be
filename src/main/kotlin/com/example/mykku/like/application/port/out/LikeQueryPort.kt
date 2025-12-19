package com.example.mykku.like.application.port.out

import com.example.mykku.board.domain.model.BoardId
import com.example.mykku.dailymessage.domain.model.DailyMessageCommentId
import com.example.mykku.feed.domain.model.FeedCommentId
import com.example.mykku.feed.domain.model.FeedId
import com.example.mykku.member.domain.model.MemberId

interface LikeQueryPort {
    fun hasLikedFeed(memberId: MemberId, feedId: FeedId): Boolean
    fun hasLikedBoard(memberId: MemberId, boardId: BoardId): Boolean
    fun hasLikedFeedComment(memberId: MemberId, feedCommentId: FeedCommentId): Boolean
    fun hasLikedDailyMessageComment(memberId: MemberId, dailyMessageCommentId: DailyMessageCommentId): Boolean

    fun countFeedLikes(feedId: FeedId): Int
    fun countBoardLikes(boardId: BoardId): Int
    fun countFeedCommentLikes(feedCommentId: FeedCommentId): Int
    fun countDailyMessageCommentLikes(dailyMessageCommentId: DailyMessageCommentId): Int
}
