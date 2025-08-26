package com.example.mykku.feed.tool

import com.example.mykku.board.domain.Board
import com.example.mykku.exception.ErrorCode
import com.example.mykku.exception.MykkuException
import com.example.mykku.feed.domain.Feed
import com.example.mykku.feed.domain.FeedComment
import com.example.mykku.feed.repository.FeedCommentRepository
import com.example.mykku.member.domain.Member
import com.example.mykku.member.domain.SocialProvider
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

@ExtendWith(MockitoExtension::class)
class FeedCommentReaderTest {

    @Mock
    private lateinit var feedCommentRepository: FeedCommentRepository

    @InjectMocks
    private lateinit var feedCommentReader: FeedCommentReader

    private fun createMockFeed(): Feed {
        val member = Member(
            id = "test_member",
            nickname = "테스트유저",
            role = "USER",
            profileImage = "profile.jpg",
            provider = SocialProvider.GOOGLE,
            socialId = "12345",
            email = "test@example.com"
        )

        val board = Board(
            id = 1L,
            title = "테스트보드",
            logo = "logo.jpg"
        )

        return Feed(
            id = 1L,
            title = "테스트 피드",
            content = "테스트 내용",
            board = board,
            member = member
        )
    }

    private fun createMockComment(id: Long = 1L, parentComment: FeedComment? = null): FeedComment {
        val feed = createMockFeed()

        return FeedComment(
            id = id,
            content = "테스트 댓글",
            feed = feed,
            parentComment = parentComment,
            member = feed.member
        )
    }

    @Test
    fun `getFeedCommentById는 존재하지 않는 댓글 ID로 조회하면 예외를 발생시킨다`() {
        val commentId = 999L

        whenever(feedCommentRepository.findById(commentId))
            .thenReturn(Optional.empty())

        val exception = assertThrows<MykkuException> {
            feedCommentReader.getFeedCommentById(commentId)
        }

        assertEquals(ErrorCode.FEED_COMMENT_NOT_FOUND, exception.errorCode)
    }

    @Test
    fun `getFeedCommentById는 존재하는 댓글 ID로 조회하면 댓글을 반환한다`() {
        val commentId = 1L
        val mockComment = createMockComment(commentId)

        whenever(feedCommentRepository.findById(commentId))
            .thenReturn(Optional.of(mockComment))

        val result = feedCommentReader.getFeedCommentById(commentId)

        assertSame(mockComment, result)
    }

    @Test
    fun `getCommentsByFeed는 피드의 최상위 댓글들을 페이지로 반환한다`() {
        val feed = createMockFeed()
        val pageable = PageRequest.of(0, 10)
        val mockComments = listOf(createMockComment())
        val mockPage = PageImpl(mockComments, pageable, mockComments.size.toLong())

        whenever(feedCommentRepository.findByFeedAndParentCommentIsNull(feed, pageable))
            .thenReturn(mockPage)

        val result = feedCommentReader.getCommentsByFeed(feed, pageable)

        assertEquals(mockPage, result)
    }

    @Test
    fun `getRepliesByParentComment는 부모 댓글의 자식 댓글들을 반환한다`() {
        val parentComment = createMockComment(1L)
        val childComments = listOf(createMockComment(2L, parentComment))

        whenever(feedCommentRepository.findByParentComment(parentComment))
            .thenReturn(childComments)

        val result = feedCommentReader.getRepliesByParentComment(parentComment)

        assertEquals(childComments, result)
    }

    @Test
    fun `getRepliesByParentComments는 빈 부모 댓글 리스트면 빈 맵을 반환한다`() {
        val result = feedCommentReader.getRepliesByParentComments(emptyList())

        assertTrue(result.isEmpty())
    }

    @Test
    fun `getRepliesByParentComments는 여러 부모 댓글들의 자식 댓글들을 그룹화하여 반환한다`() {
        val parentComment1 = createMockComment(1L)
        val parentComment2 = createMockComment(2L)
        val parentComments = listOf(parentComment1, parentComment2)

        val childComment1 = createMockComment(3L, parentComment1)
        val childComment2 = createMockComment(4L, parentComment2)
        val allReplies = listOf(childComment1, childComment2)

        whenever(feedCommentRepository.findByParentCommentIn(parentComments))
            .thenReturn(allReplies)

        val result = feedCommentReader.getRepliesByParentComments(parentComments)

        assertEquals(2, result.size)
        assertEquals(listOf(childComment1), result[1L])
        assertEquals(listOf(childComment2), result[2L])
    }

    @Test
    fun `getCommentCount는 피드의 댓글 수를 반환한다`() {
        val feed = createMockFeed()
        val expectedCount = 5L

        whenever(feedCommentRepository.countByFeed(feed))
            .thenReturn(expectedCount)

        val result = feedCommentReader.getCommentCount(feed)

        assertEquals(expectedCount, result)
    }
}
