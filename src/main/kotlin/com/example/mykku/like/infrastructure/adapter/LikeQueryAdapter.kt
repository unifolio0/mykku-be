package com.example.mykku.like.infrastructure.adapter

import com.example.mykku.board.domain.model.BoardId
import com.example.mykku.dailymessage.domain.model.DailyMessageCommentId
import com.example.mykku.feed.domain.model.FeedCommentId
import com.example.mykku.feed.domain.model.FeedId
import com.example.mykku.like.application.port.out.LikeQueryPort
import com.example.mykku.like.repository.LikeBoardRepository
import com.example.mykku.like.repository.LikeDailyMessageCommentRepository
import com.example.mykku.like.repository.LikeFeedCommentRepository
import com.example.mykku.like.repository.LikeFeedRepository
import com.example.mykku.member.domain.model.MemberId
import org.springframework.stereotype.Component

@Component
class LikeQueryAdapter(
    private val likeFeedRepository: LikeFeedRepository,
    private val likeBoardRepository: LikeBoardRepository,
    private val likeFeedCommentRepository: LikeFeedCommentRepository,
    private val likeDailyMessageCommentRepository: LikeDailyMessageCommentRepository
) : LikeQueryPort {

    override fun hasLikedFeed(memberId: MemberId, feedId: FeedId): Boolean {
        return likeFeedRepository.existsByMemberIdAndFeedId(memberId.value, feedId.value)
    }

    override fun hasLikedBoard(memberId: MemberId, boardId: BoardId): Boolean {
        return likeBoardRepository.existsByMemberIdAndBoardId(memberId.value, boardId.value)
    }

    override fun hasLikedFeedComment(memberId: MemberId, feedCommentId: FeedCommentId): Boolean {
        return likeFeedCommentRepository.existsByMemberIdAndFeedCommentId(memberId.value, feedCommentId.value)
    }

    override fun hasLikedDailyMessageComment(
        memberId: MemberId,
        dailyMessageCommentId: DailyMessageCommentId
    ): Boolean {
        return likeDailyMessageCommentRepository.existsByMemberIdAndDailyMessageCommentId(
            memberId.value,
            dailyMessageCommentId.value
        )
    }

    override fun countFeedLikes(feedId: FeedId): Int {
        return likeFeedRepository.countByFeedId(feedId.value)
    }

    override fun countBoardLikes(boardId: BoardId): Int {
        return likeBoardRepository.countByBoardId(boardId.value)
    }

    override fun countFeedCommentLikes(feedCommentId: FeedCommentId): Int {
        return likeFeedCommentRepository.countByFeedCommentId(feedCommentId.value)
    }

    override fun countDailyMessageCommentLikes(dailyMessageCommentId: DailyMessageCommentId): Int {
        return likeDailyMessageCommentRepository.countByDailyMessageCommentId(dailyMessageCommentId.value)
    }
}
