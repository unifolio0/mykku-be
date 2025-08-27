package com.example.mykku.like

import com.example.mykku.board.tool.BoardReader
import com.example.mykku.dailymessage.tool.DailyMessageCommentReader
import com.example.mykku.feed.tool.FeedCommentReader
import com.example.mykku.feed.tool.FeedReader
import com.example.mykku.like.dto.*
import com.example.mykku.like.tool.*
import com.example.mykku.member.tool.MemberReader
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class LikeService(
    private val memberReader: MemberReader,

    private val boardReader: BoardReader,
    private val likeBoardWriter: LikeBoardWriter,
    private val likeBoardReader: LikeBoardReader,

    private val dailyMessageCommentReader: DailyMessageCommentReader,
    private val likeDailyMessageCommentWriter: LikeDailyMessageCommentWriter,
    private val likeDailyMessageCommentReader: LikeDailyMessageCommentReader,

    private val feedCommentReader: FeedCommentReader,
    private val likeFeedCommentWriter: LikeFeedCommentWriter,
    private val likeFeedCommentReader: LikeFeedCommentReader,

    private val feedReader: FeedReader,
    private val likeFeedWriter: LikeFeedWriter,
    private val likeFeedReader: LikeFeedReader
) {
    @Transactional(readOnly = true)
    fun getLikedBoards(memberId: String): List<LikeBoardInfoResponse> {
        return likeBoardReader.getLikedBoards(memberId = memberId)
            .map { LikeBoardInfoResponse(it) }
    }

    @Transactional
    fun likeBoard(request: LikeBoardRequest, memberId: String): LikeBoardResponse {
        likeBoardReader.validateLikeBoardNotExists(memberId = memberId, boardId = request.boardId)
        val member = memberReader.getMemberById(memberId)
        val board = boardReader.getBoardById(request.boardId)
        val likeBoard = likeBoardWriter.createLikeBoard(board = board, member = member)
        return LikeBoardResponse(likeBoard)
    }

    @Transactional
    fun unlikeBoard(memberId: String, boardId: Long) {
        likeBoardReader.validateLikeBoardExists(memberId = memberId, boardId = boardId)
        likeBoardWriter.deleteLikeBoard(memberId = memberId, boardId = boardId)
    }

    @Transactional
    fun likeFeed(memberId: String, request: LikeFeedRequest): LikeFeedResponse {
        likeFeedReader.validateLikeFeedNotExists(memberId = memberId, feedId = request.feedId)
        val member = memberReader.getMemberById(memberId)
        val feed = feedReader.getFeedById(request.feedId)
        val likeFeed = likeFeedWriter.createLikeFeed(feed = feed, member = member)
        return LikeFeedResponse(likeFeed)
    }

    @Transactional
    fun unlikeFeed(memberId: String, feedId: Long) {
        likeFeedReader.validateLikeFeedExists(memberId = memberId, feedId = feedId)
        likeFeedWriter.deleteLikeFeed(memberId = memberId, feedId = feedId)
    }

    @Transactional
    fun likeDailyMessageComment(
        memberId: String,
        request: LikeDailyMessageCommentRequest
    ): LikeDailyMessageCommentResponse {
        likeDailyMessageCommentReader.validateLikeDailyMessageCommentNotExists(
            memberId = memberId,
            dailyMessageCommentId = request.dailyMessageCommentId
        )
        val member = memberReader.getMemberById(memberId)
        val dailyMessageComment = dailyMessageCommentReader.getDailyMessageCommentById(request.dailyMessageCommentId)
        val likeDailyMessageComment = likeDailyMessageCommentWriter.createLikeDailyMessageComment(
            dailyMessageComment = dailyMessageComment,
            member = member
        )
        return LikeDailyMessageCommentResponse(likeDailyMessageComment)
    }

    @Transactional
    fun unlikeDailyMessageComment(memberId: String, dailyMessageCommentId: Long) {
        likeDailyMessageCommentReader.validateLikeDailyMessageCommentExists(
            memberId = memberId,
            dailyMessageCommentId = dailyMessageCommentId
        )
        likeDailyMessageCommentWriter.deleteLikeDailyMessageComment(
            memberId = memberId,
            dailyMessageCommentId = dailyMessageCommentId
        )
    }

    @Transactional
    fun likeFeedComment(memberId: String, request: LikeFeedCommentRequest): LikeFeedCommentResponse {
        likeFeedCommentReader.validateLikeFeedCommentNotExists(
            memberId = memberId,
            feedCommentId = request.feedCommentId
        )
        val member = memberReader.getMemberById(memberId)
        val feedComment = feedCommentReader.getFeedCommentById(request.feedCommentId)
        val likeFeedComment = likeFeedCommentWriter.createLikeFeedComment(
            feedComment = feedComment,
            member = member
        )
        return LikeFeedCommentResponse(likeFeedComment)
    }

    @Transactional
    fun unlikeFeedComment(memberId: String, feedCommentId: Long) {
        likeFeedCommentReader.validateLikeFeedCommentExists(
            memberId = memberId,
            feedCommentId = feedCommentId
        )
        likeFeedCommentWriter.deleteLikeFeedComment(memberId = memberId, feedCommentId = feedCommentId)
    }
}
