package com.example.mykku.like

import com.example.mykku.board.application.port.out.BoardQueryPort
import com.example.mykku.dailymessage.application.port.out.DailyMessageCommentQueryPort
import com.example.mykku.feed.application.port.out.FeedCommentQueryPort
import com.example.mykku.feed.application.port.out.FeedQueryPort
import com.example.mykku.like.application.port.out.*
import com.example.mykku.like.dto.*
import com.example.mykku.member.application.port.out.MemberQueryPort
import com.example.mykku.member.domain.model.MemberId
import com.example.mykku.notification.event.FeedLikedEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class LikeService(
    private val memberQueryPort: MemberQueryPort,

    private val boardQueryPort: BoardQueryPort,
    private val likeBoardRepositoryPort: LikeBoardRepositoryPort,
    private val likeBoardQueryPort: LikeBoardQueryPort,

    private val dailyMessageCommentQueryPort: DailyMessageCommentQueryPort,
    private val likeDailyMessageCommentRepositoryPort: LikeDailyMessageCommentRepositoryPort,
    private val likeDailyMessageCommentQueryPort: LikeDailyMessageCommentQueryPort,

    private val feedCommentQueryPort: FeedCommentQueryPort,
    private val likeFeedCommentRepositoryPort: LikeFeedCommentRepositoryPort,
    private val likeFeedCommentQueryPort: LikeFeedCommentQueryPort,

    private val feedQueryPort: FeedQueryPort,
    private val likeFeedRepositoryPort: LikeFeedRepositoryPort,
    private val likeFeedQueryPort: LikeFeedQueryPort,

    private val eventPublisher: ApplicationEventPublisher
) {
    @Transactional(readOnly = true)
    fun getLikedBoards(memberId: String): List<LikeBoardInfoResponse> {
        return likeBoardQueryPort.getLikedBoards(memberId = memberId)
            .map { LikeBoardInfoResponse(it) }
    }

    @Transactional
    fun likeBoard(request: LikeBoardRequest, memberId: String): LikeBoardResponse {
        likeBoardQueryPort.validateLikeBoardNotExists(memberId = memberId, boardId = request.boardId)
        val member = memberQueryPort.getMemberById(MemberId(memberId))
        val board = boardQueryPort.getBoardById(request.boardId)
        val likeBoard = likeBoardRepositoryPort.createLikeBoard(board = board, member = member)
        return LikeBoardResponse(likeBoard)
    }

    @Transactional
    fun unlikeBoard(memberId: String, boardId: Long) {
        likeBoardQueryPort.validateLikeBoardExists(memberId = memberId, boardId = boardId)
        likeBoardRepositoryPort.deleteLikeBoard(memberId = memberId, boardId = boardId)
    }

    @Transactional
    fun likeFeed(memberId: String, request: LikeFeedRequest): LikeFeedResponse {
        likeFeedQueryPort.validateLikeFeedNotExists(memberId = memberId, feedId = request.feedId)
        val member = memberQueryPort.getMemberById(MemberId(memberId))
        val feed = feedQueryPort.getFeedById(request.feedId)
        val likeFeed = likeFeedRepositoryPort.createLikeFeed(feed = feed, member = member)

        eventPublisher.publishEvent(
            FeedLikedEvent(
                feedId = feed.id!!,
                feedAuthor = feed.member,
                liker = member
            )
        )

        return LikeFeedResponse(likeFeed)
    }

    @Transactional
    fun unlikeFeed(memberId: String, feedId: Long) {
        likeFeedQueryPort.validateLikeFeedExists(memberId = memberId, feedId = feedId)
        likeFeedRepositoryPort.deleteLikeFeed(memberId = memberId, feedId = feedId)
    }

    @Transactional
    fun likeDailyMessageComment(
        memberId: String,
        request: LikeDailyMessageCommentRequest
    ): LikeDailyMessageCommentResponse {
        likeDailyMessageCommentQueryPort.validateLikeDailyMessageCommentNotExists(
            memberId = memberId,
            dailyMessageCommentId = request.dailyMessageCommentId
        )
        val member = memberQueryPort.getMemberById(MemberId(memberId))
        val dailyMessageComment = dailyMessageCommentQueryPort.getDailyMessageCommentById(request.dailyMessageCommentId)
        val likeDailyMessageComment = likeDailyMessageCommentRepositoryPort.createLikeDailyMessageComment(
            dailyMessageComment = dailyMessageComment,
            member = member
        )
        return LikeDailyMessageCommentResponse(likeDailyMessageComment)
    }

    @Transactional
    fun unlikeDailyMessageComment(memberId: String, dailyMessageCommentId: Long) {
        likeDailyMessageCommentQueryPort.validateLikeDailyMessageCommentExists(
            memberId = memberId,
            dailyMessageCommentId = dailyMessageCommentId
        )
        likeDailyMessageCommentRepositoryPort.deleteLikeDailyMessageComment(
            memberId = memberId,
            dailyMessageCommentId = dailyMessageCommentId
        )
    }

    @Transactional
    fun likeFeedComment(memberId: String, request: LikeFeedCommentRequest): LikeFeedCommentResponse {
        likeFeedCommentQueryPort.validateLikeFeedCommentNotExists(
            memberId = memberId,
            feedCommentId = request.feedCommentId
        )
        val member = memberQueryPort.getMemberById(MemberId(memberId))
        val feedComment = feedCommentQueryPort.getFeedCommentById(request.feedCommentId)
        val likeFeedComment = likeFeedCommentRepositoryPort.createLikeFeedComment(
            feedComment = feedComment,
            member = member
        )
        return LikeFeedCommentResponse(likeFeedComment)
    }

    @Transactional
    fun unlikeFeedComment(memberId: String, feedCommentId: Long) {
        likeFeedCommentQueryPort.validateLikeFeedCommentExists(
            memberId = memberId,
            feedCommentId = feedCommentId
        )
        likeFeedCommentRepositoryPort.deleteLikeFeedComment(memberId = memberId, feedCommentId = feedCommentId)
    }
}
