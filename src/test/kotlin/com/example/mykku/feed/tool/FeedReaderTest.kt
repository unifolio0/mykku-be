package com.example.mykku.feed.tool

import com.example.mykku.BaseToolTest
import com.example.mykku.contest.repository.ContestTagRepository
import com.example.mykku.feed.domain.Feed
import com.example.mykku.feed.exception.FeedErrorCode
import com.example.mykku.feed.exception.FeedException
import com.example.mykku.feed.repository.FeedCommentRepository
import com.example.mykku.feed.repository.FeedImageRepository
import com.example.mykku.feed.repository.FeedRepository
import com.example.mykku.feed.repository.FeedTagRepository
import java.time.LocalDateTime
import java.util.Optional
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever

class FeedReaderTest : BaseToolTest() {

    @Mock
    private lateinit var feedRepository: FeedRepository

    @Mock
    private lateinit var feedImageRepository: FeedImageRepository

    @Mock
    private lateinit var feedTagRepository: FeedTagRepository

    @Mock
    private lateinit var feedCommentRepository: FeedCommentRepository

    @Mock
    private lateinit var contestTagRepository: ContestTagRepository

    @InjectMocks
    private lateinit var feedReader: FeedReader

    private fun createMockFeed(id: Long): Feed {
        return Feed(
            id = id,
            title = "테스트 피드 $id",
            content = "테스트 내용",
            board = createMockBoard(),
            member = createMockMember("member123", "테스트유저")
        )
    }

    @Test
    fun `getFeedPreviews는 피드 미리보기 목록을 반환한다`() {
        val feed1 = createMockFeed(1L)
        val feed2 = createMockFeed(2L)
        val feeds = listOf(feed1, feed2)

        whenever(feedRepository.findAll()).thenReturn(feeds)

        val result = feedReader.getFeedPreviews()

        assertEquals(2, result.size)
        assertEquals(feed1.id, result[0].id)
        assertEquals(feed1.title, result[0].title)
        assertEquals(feed2.id, result[1].id)
        assertEquals(feed2.title, result[1].title)
    }

    @Test
    fun `getFeedPreviews는 빈 목록일 때 빈 미리보기 목록을 반환한다`() {
        whenever(feedRepository.findAll()).thenReturn(emptyList())

        val result = feedReader.getFeedPreviews()

        assertTrue(result.isEmpty())
    }

    @Test
    fun `getFeedPreviews는 최대 5개의 피드 미리보기만 반환한다`() {
        val feeds = (1..10L).map { createMockFeed(it) }

        whenever(feedRepository.findAll()).thenReturn(feeds)

        val result = feedReader.getFeedPreviews()

        assertEquals(5, result.size)
        assertEquals(1L, result[0].id)
        assertEquals(5L, result[4].id)
    }

    @Test
    fun `getFeedById는 유효한 feedId로 피드를 반환한다`() {
        val feedId = 1L
        val feed = createMockFeed(feedId)

        whenever(feedRepository.findById(feedId)).thenReturn(Optional.of(feed))

        val result = feedReader.getFeedById(feedId)

        assertEquals(feed, result)
        assertEquals(feedId, result.id)
    }

    @Test
    fun `getFeedById는 존재하지 않는 feedId로 FEED_NOT_FOUND 예외를 발생시킨다`() {
        val feedId = 999L

        whenever(feedRepository.findById(feedId)).thenReturn(Optional.empty())

        val exception = assertThrows<FeedException> {
            feedReader.getFeedById(feedId)
        }

        assertEquals(FeedErrorCode.FEED_NOT_FOUND, exception.errorCode)
    }

    @Test
    fun `getPopularFeedsByBoard는 인기 피드 목록을 반환한다`() {
        val board = createMockBoard()
        val feed1 = createMockFeed(1L)
        val feed2 = createMockFeed(2L)

        whenever(feedRepository.findPopularFeedsByBoardSince(eq(board), any<LocalDateTime>(), any()))
            .thenReturn(listOf(feed1, feed2))

        val result = feedReader.getPopularFeedsByBoard(board)

        assertEquals(2, result.size)
        assertEquals(feed1.id, result[0].id)
        assertEquals(feed2.id, result[1].id)
    }

    @Test
    fun `getPopularFeedsByBoard는 인기 피드가 없으면 빈 목록을 반환한다`() {
        val board = createMockBoard()

        whenever(feedRepository.findPopularFeedsByBoardSince(eq(board), any<LocalDateTime>(), any()))
            .thenReturn(emptyList())

        val result = feedReader.getPopularFeedsByBoard(board)

        assertTrue(result.isEmpty())
    }
}
