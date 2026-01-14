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
import com.example.mykku.member.domain.Member
import com.example.mykku.notification.event.FeedLikedEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class LikeService(
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
    fun getLikedBoards(member: Member, pageable: Pageable): Page<LikeBoardInfoResponse> {
        return likeBoardReader.getLikedBoards(memberId = member.id, pageable = pageable)
            .map { LikeBoardInfoResponse(it) }
    }

    @Transactional
    fun likeBoard(boardId: Long, member: Member): LikeBoardResponse {
        likeBoardReader.validateLikeBoardNotExists(memberId = member.id, boardId = boardId)
        val board = boardReader.getBoardById(boardId)
        val likeBoard = likeBoardWriter.createLikeBoard(board = board, member = member)
        return LikeBoardResponse(likeBoard)
    }

    @Transactional
    fun unlikeBoard(member: Member, boardId: Long) {
        likeBoardReader.validateLikeBoardExists(memberId = member.id, boardId = boardId)
        likeBoardWriter.deleteLikeBoard(memberId = member.id, boardId = boardId)
    }

    @Transactional
    fun likeFeed(member: Member, feedId: Long): LikeFeedResponse {
        likeFeedReader.validateLikeFeedNotExists(memberId = member.id, feedId = feedId)
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
    fun unlikeFeed(member: Member, feedId: Long) {
        likeFeedReader.validateLikeFeedExists(memberId = member.id, feedId = feedId)
        likeFeedWriter.deleteLikeFeed(memberId = member.id, feedId = feedId)
    }

    @Transactional
    fun likeDailyMessageComment(
        member: Member,
        dailyMessageCommentId: Long
    ): LikeDailyMessageCommentResponse {
        likeDailyMessageCommentReader.validateLikeDailyMessageCommentNotExists(
            memberId = member.id,
            dailyMessageCommentId = dailyMessageCommentId
        )
        val dailyMessageComment = dailyMessageCommentReader.getDailyMessageCommentById(dailyMessageCommentId)
        val likeDailyMessageComment = likeDailyMessageCommentWriter.createLikeDailyMessageComment(
            dailyMessageComment = dailyMessageComment,
            member = member
        )
        return LikeDailyMessageCommentResponse(likeDailyMessageComment)
    }

    @Transactional
    fun unlikeDailyMessageComment(member: Member, dailyMessageCommentId: Long) {
        likeDailyMessageCommentReader.validateLikeDailyMessageCommentExists(
            memberId = member.id,
            dailyMessageCommentId = dailyMessageCommentId
        )
        likeDailyMessageCommentWriter.deleteLikeDailyMessageComment(
            memberId = member.id,
            dailyMessageCommentId = dailyMessageCommentId
        )
    }

    @Transactional
    fun likeFeedComment(member: Member, feedCommentId: Long): LikeFeedCommentResponse {
        likeFeedCommentReader.validateLikeFeedCommentNotExists(
            memberId = member.id,
            feedCommentId = feedCommentId
        )
        val feedComment = feedCommentReader.getFeedCommentById(feedCommentId)
        val likeFeedComment = likeFeedCommentWriter.createLikeFeedComment(
            feedComment = feedComment,
            member = member
        )
        return LikeFeedCommentResponse(likeFeedComment)
    }

    @Transactional
    fun unlikeFeedComment(member: Member, feedCommentId: Long) {
        likeFeedCommentReader.validateLikeFeedCommentExists(
            memberId = member.id,
            feedCommentId = feedCommentId
        )
        likeFeedCommentWriter.deleteLikeFeedComment(memberId = member.id, feedCommentId = feedCommentId)
    }
}
