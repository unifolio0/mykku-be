package com.example.mykku.feed

import com.example.mykku.BaseServiceTest
import com.example.mykku.board.domain.Board
import com.example.mykku.feed.application.port.out.FeedCommentQueryPort
import com.example.mykku.feed.application.port.out.FeedCommentRepositoryPort
import com.example.mykku.feed.application.port.out.FeedQueryPort
import com.example.mykku.feed.domain.Feed
import com.example.mykku.feed.domain.FeedComment
import com.example.mykku.feed.dto.CreateFeedCommentRequest
import com.example.mykku.feed.dto.UpdateFeedCommentRequest
import com.example.mykku.feed.exception.FeedException
import com.example.mykku.feed.exception.FeedErrorCode
import com.example.mykku.like.tool.LikeFeedCommentReader
import com.example.mykku.member.application.port.out.MemberQueryPort
import com.example.mykku.member.domain.Member
import com.example.mykku.member.domain.model.MemberId
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import kotlin.test.assertEquals

class FeedCommentServiceTest : BaseServiceTest() {

    @Mock
    private lateinit var feedQueryPort: FeedQueryPort

    @Mock
    private lateinit var feedCommentQueryPort: FeedCommentQueryPort

    @Mock
    private lateinit var feedCommentRepositoryPort: FeedCommentRepositoryPort

    @Mock
    private lateinit var likeFeedCommentReader: LikeFeedCommentReader

    @Mock
    private lateinit var memberQueryPort: MemberQueryPort

    @InjectMocks
    private lateinit var feedCommentService: FeedCommentService

    private val member = createTestMember(id = "member1", nickname = "testUser", email = "test@test.com")
    private val board = createTestBoard(id = 1L, title = "테스트 보드", logo = "")

    private val feed = createTestFeed()

    private fun createTestFeed(): Feed {
        val feed = Feed(
            id = 1L,
            title = "테스트 피드",
            content = "내용",
            board = board,
            member = member
        )
        initializeBaseEntityFieldsFromSuperclass(feed)
        return feed
    }

    private fun createTestFeedComment(
        id: Long = 1L,
        content: String,
        feed: Feed,
        member: Member,
        parentComment: FeedComment? = null
    ): FeedComment {
        val comment = FeedComment(
            id = id,
            content = content,
            feed = feed,
            member = member,
            parentComment = parentComment
        )
        initializeBaseEntityFieldsFromSuperclass(comment)
        return comment
    }

    @Test
    fun `getComments - 피드 댓글 목록을 반환한다`() {
        // given
        val pageable: Pageable = PageRequest.of(0, 10)
        val parentComment = createTestFeedComment(
            content = "부모 댓글",
            feed = feed,
            member = member,
            parentComment = null
        )

        val replyComment = createTestFeedComment(
            id = 2L,
            content = "대댓글",
            feed = feed,
            member = member,
            parentComment = parentComment
        )

        val commentsPage = PageImpl(listOf(parentComment), pageable, 1)
        val repliesMap = mapOf(1L to listOf(replyComment))

        whenever(feedQueryPort.getFeedById(1L)).thenReturn(feed)
        whenever(feedCommentQueryPort.getCommentsByFeed(feed, pageable)).thenReturn(commentsPage)
        whenever(feedCommentQueryPort.getRepliesByParentComments(listOf(parentComment))).thenReturn(repliesMap)
        whenever(likeFeedCommentReader.isLiked("member1", parentComment)).thenReturn(false)
        whenever(likeFeedCommentReader.isLiked("member1", replyComment)).thenReturn(false)

        // when
        val result = feedCommentService.getComments(1L, "member1", pageable)

        // then
        assertEquals(1, result.comments.size)
        assertEquals(1, result.comments[0].replies.size)
        assertEquals(parentComment.content, result.comments[0].content)
        assertEquals(replyComment.content, result.comments[0].replies[0].content)
        assertEquals(1L, result.totalElements)
        assertEquals(1, result.totalPages)
        assertEquals(0, result.currentPage)
        assertEquals(10, result.pageSize)
        assertEquals(false, result.hasNext)
    }

    @Test
    fun `getComments - 댓글이 없는 피드를 반환한다`() {
        // given
        val pageable: Pageable = PageRequest.of(0, 10)
        val commentsPage = PageImpl<FeedComment>(emptyList(), pageable, 0)

        whenever(feedQueryPort.getFeedById(1L)).thenReturn(feed)
        whenever(feedCommentQueryPort.getCommentsByFeed(feed, pageable)).thenReturn(commentsPage)
        whenever(feedCommentQueryPort.getRepliesByParentComments(emptyList())).thenReturn(emptyMap())

        // when
        val result = feedCommentService.getComments(1L, "member1", pageable)

        // then
        assertEquals(0, result.comments.size)
        assertEquals(0L, result.totalElements)
        assertEquals(0, result.totalPages)
        assertEquals(0, result.currentPage)
        assertEquals(10, result.pageSize)
        assertEquals(false, result.hasNext)
    }

    @Test
    fun `getComments - 비로그인 사용자의 경우 좋아요 정보는 false로 반환한다`() {
        // given
        val pageable: Pageable = PageRequest.of(0, 10)
        val parentComment = createTestFeedComment(
            content = "부모 댓글",
            feed = feed,
            member = member,
            parentComment = null
        )

        val commentsPage = PageImpl(listOf(parentComment), pageable, 1)

        whenever(feedQueryPort.getFeedById(1L)).thenReturn(feed)
        whenever(feedCommentQueryPort.getCommentsByFeed(feed, pageable)).thenReturn(commentsPage)
        whenever(feedCommentQueryPort.getRepliesByParentComments(listOf(parentComment))).thenReturn(emptyMap())

        // when
        val result = feedCommentService.getComments(1L, null, pageable)

        // then
        assertEquals(1, result.comments.size)
        assertEquals(false, result.comments[0].isLiked)
    }

    @Test
    fun `createComment - 댓글을 생성한다`() {
        // given
        val request = CreateFeedCommentRequest(content = "새 댓글", parentCommentId = null)
        val savedComment = createTestFeedComment(
            id = 10L,
            content = "새 댓글",
            feed = feed,
            member = member,
            parentComment = null
        )

        whenever(feedQueryPort.getFeedById(1L)).thenReturn(feed)
        whenever(memberQueryPort.getMemberById(MemberId("member1"))).thenReturn(member)
        whenever(feedCommentRepositoryPort.createComment("새 댓글", feed, member, null)).thenReturn(savedComment)

        // when
        val result = feedCommentService.createComment(1L, "member1", request)

        // then
        assertEquals(10L, result.id)
        assertEquals("새 댓글", result.content)
        assertEquals("member1", result.author.memberId)
        assertEquals("testUser", result.author.nickname)
        verify(feedCommentRepositoryPort).createComment("새 댓글", feed, member, null)
    }

    @Test
    fun `createComment - 답글을 생성한다`() {
        // given
        val parentComment = createTestFeedComment(
            id = 1L,
            content = "부모 댓글",
            feed = feed,
            member = member,
            parentComment = null
        )
        val request = CreateFeedCommentRequest(content = "답글", parentCommentId = 1L)
        val savedReply = createTestFeedComment(
            id = 11L,
            content = "답글",
            feed = feed,
            member = member,
            parentComment = parentComment
        )

        whenever(feedQueryPort.getFeedById(1L)).thenReturn(feed)
        whenever(memberQueryPort.getMemberById(MemberId("member1"))).thenReturn(member)
        whenever(feedCommentQueryPort.getFeedCommentById(1L)).thenReturn(parentComment)
        whenever(feedCommentRepositoryPort.createComment("답글", feed, member, parentComment)).thenReturn(savedReply)

        // when
        val result = feedCommentService.createComment(1L, "member1", request)

        // then
        assertEquals(11L, result.id)
        assertEquals("답글", result.content)
        verify(feedCommentRepositoryPort).createComment("답글", feed, member, parentComment)
    }

    @Test
    fun `updateComment - 댓글을 수정한다`() {
        // given
        val comment = createTestFeedComment(
            id = 1L,
            content = "원본 댓글",
            feed = feed,
            member = member,
            parentComment = null
        )
        val request = UpdateFeedCommentRequest(content = "수정된 댓글")
        val updatedComment = createTestFeedComment(
            id = 1L,
            content = "수정된 댓글",
            feed = feed,
            member = member,
            parentComment = null
        )

        whenever(feedCommentQueryPort.getFeedCommentById(1L)).thenReturn(comment)
        whenever(feedCommentRepositoryPort.updateComment(comment, "수정된 댓글")).thenReturn(updatedComment)

        // when
        val result = feedCommentService.updateComment(1L, "member1", request)

        // then
        assertEquals(1L, result.id)
        assertEquals("수정된 댓글", result.content)
        verify(feedCommentRepositoryPort).updateComment(comment, "수정된 댓글")
    }

    @Test
    fun `updateComment - 권한이 없으면 예외를 발생시킨다`() {
        // given
        val comment = createTestFeedComment(
            id = 1L,
            content = "원본 댓글",
            feed = feed,
            member = member,
            parentComment = null
        )
        val request = UpdateFeedCommentRequest(content = "수정된 댓글")

        whenever(feedCommentQueryPort.getFeedCommentById(1L)).thenReturn(comment)

        // when & then
        val exception = assertThrows<FeedException> {
            feedCommentService.updateComment(1L, "otherMember", request)
        }
        assertEquals(FeedErrorCode.FEED_COMMENT_FORBIDDEN_ACCESS, exception.errorCode)
    }

    @Test
    fun `deleteComment - 댓글을 삭제한다`() {
        // given
        val comment = createTestFeedComment(
            id = 1L,
            content = "삭제할 댓글",
            feed = feed,
            member = member,
            parentComment = null
        )

        whenever(feedCommentQueryPort.getFeedCommentById(1L)).thenReturn(comment)

        // when
        feedCommentService.deleteComment(1L, "member1")

        // then
        verify(feedCommentRepositoryPort).deleteComment(comment)
    }

    @Test
    fun `deleteComment - 권한이 없으면 예외를 발생시킨다`() {
        // given
        val comment = createTestFeedComment(
            id = 1L,
            content = "삭제할 댓글",
            feed = feed,
            member = member,
            parentComment = null
        )

        whenever(feedCommentQueryPort.getFeedCommentById(1L)).thenReturn(comment)

        // when & then
        val exception = assertThrows<FeedException> {
            feedCommentService.deleteComment(1L, "otherMember")
        }
        assertEquals(FeedErrorCode.FEED_COMMENT_FORBIDDEN_ACCESS, exception.errorCode)
    }
}
