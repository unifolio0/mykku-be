package com.example.mykku.feed.tool

import com.example.mykku.BaseToolTest
import com.example.mykku.feed.exception.FeedException
import com.example.mykku.feed.exception.FeedErrorCode
import com.example.mykku.feed.domain.Feed
import com.example.mykku.feed.domain.FeedImage
import com.example.mykku.feed.domain.FeedTag
import com.example.mykku.feed.repository.FeedImageRepository
import com.example.mykku.feed.repository.FeedRepository
import com.example.mykku.feed.repository.FeedTagRepository
import com.example.mykku.image.dto.ImageUploadResult
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals

class FeedWriterTest : BaseToolTest() {

    @Mock
    private lateinit var feedRepository: FeedRepository

    @Mock
    private lateinit var feedImageRepository: FeedImageRepository

    @Mock
    private lateinit var feedTagRepository: FeedTagRepository

    @InjectMocks
    private lateinit var feedWriter: FeedWriter

    @Test
    fun `createFeed는 피드, 이미지, 태그를 생성하고 저장한다`() {
        val member = createMockMember("member123", "테스트유저")
        val board = createMockBoard()
        val imageResults = listOf(
            ImageUploadResult(url = "image1.jpg", width = 100, height = 100),
            ImageUploadResult(url = "image2.jpg", width = 200, height = 200)
        )
        val tagTitles = listOf("태그1", "태그2", "태그3")
        
        val mockFeed = Feed(
            id = 1L,
            title = "테스트 피드",
            content = "테스트 내용",
            board = board,
            member = member
        )
        
        whenever(feedRepository.save(any<Feed>())).thenReturn(mockFeed)
        whenever(feedImageRepository.saveAll(any<List<FeedImage>>()))
            .thenAnswer { invocation -> invocation.arguments[0] }
        whenever(feedTagRepository.saveAll(any<List<FeedTag>>()))
            .thenAnswer { invocation -> invocation.arguments[0] }

        val result = feedWriter.createFeed("테스트 피드", "테스트 내용", board, member, imageResults, tagTitles)

        assertEquals(mockFeed, result.first)
        assertEquals(2, result.second.size)
        assertEquals(3, result.third.size)
    }

    @Test
    fun `createFeed는 이미지 개수가 최대치를 초과하면 예외를 발생시킨다`() {
        val member = createMockMember("member123", "테스트유저")
        val board = createMockBoard()
        val imageResults = List(Feed.IMAGE_MAX_COUNT + 1) {
            ImageUploadResult(url = "image$it.jpg", width = 100, height = 100)
        }
        val tagTitles = listOf("태그1")

        val exception = assertThrows<FeedException> {
            feedWriter.createFeed("테스트 피드", "테스트 내용", board, member, imageResults, tagTitles)
        }

        assertEquals(FeedErrorCode.FEED_IMAGE_LIMIT_EXCEEDED, exception.errorCode)
    }

    @Test
    fun `createFeed는 태그 개수가 최대치를 초과하면 예외를 발생시킨다`() {
        val member = createMockMember("member123", "테스트유저")
        val board = createMockBoard()
        val imageResults = listOf(ImageUploadResult(url = "image.jpg", width = 100, height = 100))
        val tagTitles = List(Feed.TAG_MAX_COUNT + 1) { "태그$it" }

        val exception = assertThrows<FeedException> {
            feedWriter.createFeed("테스트 피드", "테스트 내용", board, member, imageResults, tagTitles)
        }

        assertEquals(FeedErrorCode.FEED_TAG_LIMIT_EXCEEDED, exception.errorCode)
    }

    @Test
    fun `createFeed는 이미지 크기가 유효하지 않으면 예외를 발생시킨다`() {
        val member = createMockMember("member123", "테스트유저")
        val board = createMockBoard()
        val imageResults = listOf(
            ImageUploadResult(url = "image.jpg", width = 0, height = 100)
        )
        val tagTitles = listOf("태그1")
        
        val mockFeed = Feed(
            id = 1L,
            title = "테스트 피드",
            content = "테스트 내용",
            board = board,
            member = member
        )
        
        whenever(feedRepository.save(any<Feed>())).thenReturn(mockFeed)

        val exception = assertThrows<FeedException> {
            feedWriter.createFeed("테스트 피드", "테스트 내용", board, member, imageResults, tagTitles)
        }

        assertEquals(FeedErrorCode.IMAGE_INVALID_DIMENSIONS, exception.errorCode)
    }

    @Test
    fun `createFeed는 중복된 태그를 제거하고 저장한다`() {
        val member = createMockMember("member123", "테스트유저")
        val board = createMockBoard()
        val imageResults = emptyList<ImageUploadResult>()
        val tagTitles = listOf("태그1", "태그1", "태그2", " 태그2 ", "")
        
        val mockFeed = Feed(
            id = 1L,
            title = "테스트 피드",
            content = "테스트 내용",
            board = board,
            member = member
        )
        
        whenever(feedRepository.save(any<Feed>())).thenReturn(mockFeed)
        whenever(feedImageRepository.saveAll(any<List<FeedImage>>()))
            .thenAnswer { invocation -> invocation.arguments[0] }
        whenever(feedTagRepository.saveAll(any<List<FeedTag>>()))
            .thenAnswer { invocation -> invocation.arguments[0] }

        val result = feedWriter.createFeed("테스트 피드", "테스트 내용", board, member, imageResults, tagTitles)

        assertEquals(2, result.third.size) // "태그1", "태그2"만 저장
    }

    @Test
    fun `createFeed는 빈 이미지와 태그 리스트로도 피드를 생성할 수 있다`() {
        val member = createMockMember("member123", "테스트유저")
        val board = createMockBoard()
        val imageResults = emptyList<ImageUploadResult>()
        val tagTitles = emptyList<String>()

        val mockFeed = Feed(
            id = 1L,
            title = "테스트 피드",
            content = "테스트 내용",
            board = board,
            member = member
        )

        whenever(feedRepository.save(any<Feed>())).thenReturn(mockFeed)
        whenever(feedImageRepository.saveAll(any<List<FeedImage>>()))
            .thenAnswer { invocation -> invocation.arguments[0] }
        whenever(feedTagRepository.saveAll(any<List<FeedTag>>()))
            .thenAnswer { invocation -> invocation.arguments[0] }

        val result = feedWriter.createFeed("테스트 피드", "테스트 내용", board, member, imageResults, tagTitles)

        assertEquals(mockFeed, result.first)
        assertEquals(0, result.second.size)
        assertEquals(0, result.third.size)
    }

    @Test
    fun `deleteFeed는 피드와 관련 이미지, 태그를 삭제한다`() {
        // given
        val member = createMockMember("member123", "테스트유저")
        val board = createMockBoard()
        val feed = Feed(
            id = 1L,
            title = "테스트 피드",
            content = "테스트 내용",
            board = board,
            member = member
        )

        // when
        feedWriter.deleteFeed(feed, member)

        // then
        verify(feedTagRepository).deleteAllByFeed(feed)
        verify(feedImageRepository).deleteAllByFeed(feed)
        verify(feedRepository).delete(feed)
    }

    @Test
    fun `deleteFeed는 작성자가 아닌 경우 예외를 발생시킨다`() {
        // given
        val owner = createMockMember("owner-id", "소유자")
        val otherMember = createMockMember("other-id", "다른사용자")
        val board = createMockBoard()
        val feed = Feed(
            id = 1L,
            title = "테스트 피드",
            content = "테스트 내용",
            board = board,
            member = owner
        )

        // when & then
        val exception = assertThrows<FeedException> {
            feedWriter.deleteFeed(feed, otherMember)
        }
        assertEquals(FeedErrorCode.FEED_FORBIDDEN_ACCESS, exception.errorCode)
    }
}