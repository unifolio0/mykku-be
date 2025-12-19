package com.example.mykku.like

import com.example.mykku.BaseServiceTest
import com.example.mykku.board.application.port.out.BoardQueryPort
import com.example.mykku.dailymessage.domain.DailyMessage
import com.example.mykku.dailymessage.domain.DailyMessageComment
import com.example.mykku.dailymessage.application.port.out.DailyMessageCommentQueryPort
import com.example.mykku.feed.application.port.out.FeedCommentQueryPort
import com.example.mykku.feed.application.port.out.FeedQueryPort
import com.example.mykku.feed.domain.Feed
import com.example.mykku.feed.domain.FeedComment
import com.example.mykku.like.domain.LikeBoard
import com.example.mykku.like.domain.LikeDailyMessageComment
import com.example.mykku.like.domain.LikeFeed
import com.example.mykku.like.domain.LikeFeedComment
import com.example.mykku.like.dto.LikeBoardRequest
import com.example.mykku.like.dto.LikeDailyMessageCommentRequest
import com.example.mykku.like.dto.LikeFeedCommentRequest
import com.example.mykku.like.dto.LikeFeedRequest
import com.example.mykku.like.application.port.out.*
import com.example.mykku.member.application.port.out.MemberQueryPort
import com.example.mykku.member.domain.model.MemberId
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.whenever
import org.springframework.context.ApplicationEventPublisher
import java.time.LocalDate
import kotlin.test.assertEquals

class LikeServiceTest : BaseServiceTest() {

    @Mock
    private lateinit var memberQueryPort: MemberQueryPort

    @Mock
    private lateinit var boardQueryPort: BoardQueryPort

    @Mock
    private lateinit var likeBoardRepositoryPort: LikeBoardRepositoryPort

    @Mock
    private lateinit var likeBoardQueryPort: LikeBoardQueryPort

    @Mock
    private lateinit var dailyMessageCommentQueryPort: DailyMessageCommentQueryPort

    @Mock
    private lateinit var likeDailyMessageCommentRepositoryPort: LikeDailyMessageCommentRepositoryPort

    @Mock
    private lateinit var likeDailyMessageCommentQueryPort: LikeDailyMessageCommentQueryPort

    @Mock
    private lateinit var feedCommentQueryPort: FeedCommentQueryPort

    @Mock
    private lateinit var likeFeedCommentRepositoryPort: LikeFeedCommentRepositoryPort

    @Mock
    private lateinit var likeFeedCommentQueryPort: LikeFeedCommentQueryPort

    @Mock
    private lateinit var feedQueryPort: FeedQueryPort

    @Mock
    private lateinit var likeFeedRepositoryPort: LikeFeedRepositoryPort

    @Mock
    private lateinit var likeFeedQueryPort: LikeFeedQueryPort

    @Mock
    private lateinit var eventPublisher: ApplicationEventPublisher

    @InjectMocks
    private lateinit var likeService: LikeService

    private val member = createTestMember(id = "member1", nickname = "testUser", email = "test@test.com")
    private val board = createTestBoard(id = 1L, title = "테스트 보드", logo = "")

    @Test
    fun `getLikedBoards - 좋아요한 보드 목록을 반환한다`() {
        // given
        val likeBoard = LikeBoard(id = 1L, member = member, board = board)
        val likedBoards = listOf(likeBoard)

        whenever(likeBoardQueryPort.getLikedBoards(memberId = "member1")).thenReturn(likedBoards)

        // when
        val result = likeService.getLikedBoards("member1")

        // then
        assertEquals(1, result.size)
    }

    @Test
    fun `likeBoard - 보드에 좋아요를 추가한다`() {
        // given
        val request = LikeBoardRequest(boardId = 1L)
        val likeBoard = LikeBoard(id = 1L, member = member, board = board)

        whenever(memberQueryPort.getMemberById(MemberId("member1"))).thenReturn(member)
        whenever(boardQueryPort.getBoardById(1L)).thenReturn(board)
        whenever(likeBoardRepositoryPort.createLikeBoard(board = board, member = member)).thenReturn(likeBoard)

        // when
        val result = likeService.likeBoard(request, "member1")

        // then
        assertEquals(likeBoard.id, result.id)
    }

    @Test
    fun `unlikeBoard - 보드 좋아요를 취소한다`() {
        // when
        likeService.unlikeBoard("member1", 1L)

        // then - 예외가 발생하지 않으면 성공
    }

    @Test
    fun `likeFeed - 피드에 좋아요를 추가한다`() {
        // given
        val feed = Feed(
            id = 1L,
            title = "테스트 피드",
            content = "내용",
            board = board,
            member = member
        )
        val request = LikeFeedRequest(feedId = 1L)
        val likeFeed = LikeFeed(id = 1L, member = member, feed = feed)

        whenever(memberQueryPort.getMemberById(MemberId("member1"))).thenReturn(member)
        whenever(feedQueryPort.getFeedById(1L)).thenReturn(feed)
        whenever(likeFeedRepositoryPort.createLikeFeed(feed = feed, member = member)).thenReturn(likeFeed)

        // when
        val result = likeService.likeFeed("member1", request)

        // then
        assertEquals(likeFeed.id, result.id)
    }

    @Test
    fun `unlikeFeed - 피드 좋아요를 취소한다`() {
        // when
        likeService.unlikeFeed("member1", 1L)

        // then - 예외가 발생하지 않으면 성공
    }

    @Test
    fun `likeDailyMessageComment - 일일 메시지 댓글에 좋아요를 추가한다`() {
        // given
        val dailyMessage = DailyMessage(
            id = 1L,
            title = "제목",
            content = "내용",
            date = LocalDate.now()
        )
        val dailyMessageComment = DailyMessageComment(
            id = 1L,
            content = "댓글",
            dailyMessage = dailyMessage,
            member = member,
            parentComment = null
        )
        val request = LikeDailyMessageCommentRequest(dailyMessageCommentId = 1L)
        val likeDailyMessageComment = LikeDailyMessageComment(
            id = 1L,
            member = member,
            dailyMessageComment = dailyMessageComment
        )

        whenever(memberQueryPort.getMemberById(MemberId("member1"))).thenReturn(member)
        whenever(dailyMessageCommentQueryPort.getDailyMessageCommentById(1L)).thenReturn(dailyMessageComment)
        whenever(
            likeDailyMessageCommentRepositoryPort.createLikeDailyMessageComment(
                dailyMessageComment = dailyMessageComment,
                member = member
            )
        ).thenReturn(likeDailyMessageComment)

        // when
        val result = likeService.likeDailyMessageComment("member1", request)

        // then
        assertEquals(likeDailyMessageComment.id, result.id)
    }

    @Test
    fun `unlikeDailyMessageComment - 일일 메시지 댓글 좋아요를 취소한다`() {
        // when
        likeService.unlikeDailyMessageComment("member1", 1L)

        // then - 예외가 발생하지 않으면 성공
    }

    @Test
    fun `likeFeedComment - 피드 댓글에 좋아요를 추가한다`() {
        // given
        val feed = Feed(
            id = 1L,
            title = "테스트 피드",
            content = "내용",
            board = board,
            member = member
        )
        val feedComment = FeedComment(
            id = 1L,
            content = "댓글",
            feed = feed,
            member = member,
            parentComment = null
        )
        val request = LikeFeedCommentRequest(feedCommentId = 1L)
        val likeFeedComment = LikeFeedComment(id = 1L, member = member, feedComment = feedComment)

        whenever(memberQueryPort.getMemberById(MemberId("member1"))).thenReturn(member)
        whenever(feedCommentQueryPort.getFeedCommentById(1L)).thenReturn(feedComment)
        whenever(
            likeFeedCommentRepositoryPort.createLikeFeedComment(
                feedComment = feedComment,
                member = member
            )
        ).thenReturn(likeFeedComment)

        // when
        val result = likeService.likeFeedComment("member1", request)

        // then
        assertEquals(likeFeedComment.id, result.id)
    }

    @Test
    fun `unlikeFeedComment - 피드 댓글 좋아요를 취소한다`() {
        // when
        likeService.unlikeFeedComment("member1", 1L)

        // then - 예외가 발생하지 않으면 성공
    }
}
