package com.example.mykku.like

import com.example.mykku.board.tool.BoardReader
import com.example.mykku.dailymessage.tool.DailyMessageCommentReader
import com.example.mykku.feed.tool.FeedCommentReader
import com.example.mykku.feed.tool.FeedReader
import com.example.mykku.like.dto.LikeBoardInfoResponse
import com.example.mykku.like.dto.LikeBoardResponse
import com.example.mykku.like.dto.LikeDailyMessageCommentResponse
import com.example.mykku.like.dto.LikeFeedCommentResponse
import com.example.mykku.like.dto.LikeFeedResponse
import com.example.mykku.like.tool.LikeBoardReader
import com.example.mykku.like.tool.LikeBoardWriter
import com.example.mykku.like.tool.LikeDailyMessageCommentReader
import com.example.mykku.like.tool.LikeDailyMessageCommentWriter
import com.example.mykku.like.tool.LikeFeedCommentReader
import com.example.mykku.like.tool.LikeFeedCommentWriter
import com.example.mykku.like.tool.LikeFeedReader
import com.example.mykku.like.tool.LikeFeedWriter
import com.example.mykku.member.tool.MemberReader
import com.example.mykku.notification.event.FeedLikedEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
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
    private val likeFeedReader: LikeFeedReader,

    private val eventPublisher: ApplicationEventPublisher
) {
    @Transactional(readOnly = true)
    fun getLikedBoards(memberId: String, pageable: Pageable): Page<LikeBoardInfoResponse> {
        return likeBoardReader.getLikedBoards(memberId = memberId, pageable = pageable)
            .map { LikeBoardInfoResponse(it) }
    }

    @Transactional
    fun likeBoard(boardId: Long, memberId: String): LikeBoardResponse {
        likeBoardReader.validateLikeBoardNotExists(memberId = memberId, boardId = boardId)
        val member = memberReader.getMemberById(memberId)
        val board = boardReader.getBoardById(boardId)
        val likeBoard = likeBoardWriter.createLikeBoard(board = board, member = member)
        return LikeBoardResponse(likeBoard)
    }

    @Transactional
    fun unlikeBoard(memberId: String, boardId: Long) {
        likeBoardReader.validateLikeBoardExists(memberId = memberId, boardId = boardId)
        likeBoardWriter.deleteLikeBoard(memberId = memberId, boardId = boardId)
    }

    @Transactional
    fun likeFeed(memberId: String, feedId: Long): LikeFeedResponse {
        likeFeedReader.validateLikeFeedNotExists(memberId = memberId, feedId = feedId)
        val member = memberReader.getMemberById(memberId)
        val feed = feedReader.getFeedById(feedId)
        val likeFeed = likeFeedWriter.createLikeFeed(feed = feed, member = member)

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
        likeFeedReader.validateLikeFeedExists(memberId = memberId, feedId = feedId)
        likeFeedWriter.deleteLikeFeed(memberId = memberId, feedId = feedId)
    }

    @Transactional
    fun likeDailyMessageComment(
        memberId: String,
        dailyMessageCommentId: Long
    ): LikeDailyMessageCommentResponse {
        likeDailyMessageCommentReader.validateLikeDailyMessageCommentNotExists(
            memberId = memberId,
            dailyMessageCommentId = dailyMessageCommentId
        )
        val member = memberReader.getMemberById(memberId)
        val dailyMessageComment = dailyMessageCommentReader.getDailyMessageCommentById(dailyMessageCommentId)
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
    fun likeFeedComment(memberId: String, feedCommentId: Long): LikeFeedCommentResponse {
        likeFeedCommentReader.validateLikeFeedCommentNotExists(
            memberId = memberId,
            feedCommentId = feedCommentId
        )
        val member = memberReader.getMemberById(memberId)
        val feedComment = feedCommentReader.getFeedCommentById(feedCommentId)
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
