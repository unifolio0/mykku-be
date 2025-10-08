package com.example.mykku.feed

import com.example.mykku.BaseServiceTest
import com.example.mykku.board.domain.Board
import com.example.mykku.feed.domain.Feed
import com.example.mykku.feed.domain.FeedComment
import com.example.mykku.feed.tool.FeedCommentReader
import com.example.mykku.feed.tool.FeedReader
import com.example.mykku.like.tool.LikeFeedCommentReader
import com.example.mykku.member.domain.Member
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.whenever
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import kotlin.test.assertEquals

class FeedCommentServiceTest : BaseServiceTest() {

    @Mock
    private lateinit var feedReader: FeedReader

    @Mock
    private lateinit var feedCommentReader: FeedCommentReader

    @Mock
    private lateinit var likeFeedCommentReader: LikeFeedCommentReader

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

        whenever(feedReader.getFeedById(1L)).thenReturn(feed)
        whenever(feedCommentReader.getCommentsByFeed(feed, pageable)).thenReturn(commentsPage)
        whenever(feedCommentReader.getRepliesByParentComments(listOf(parentComment))).thenReturn(repliesMap)
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

        whenever(feedReader.getFeedById(1L)).thenReturn(feed)
        whenever(feedCommentReader.getCommentsByFeed(feed, pageable)).thenReturn(commentsPage)
        whenever(feedCommentReader.getRepliesByParentComments(emptyList())).thenReturn(emptyMap())

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

        whenever(feedReader.getFeedById(1L)).thenReturn(feed)
        whenever(feedCommentReader.getCommentsByFeed(feed, pageable)).thenReturn(commentsPage)
        whenever(feedCommentReader.getRepliesByParentComments(listOf(parentComment))).thenReturn(emptyMap())

        // when
        val result = feedCommentService.getComments(1L, null, pageable)

        // then
        assertEquals(1, result.comments.size)
        assertEquals(false, result.comments[0].isLiked)
    }
}