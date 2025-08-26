package com.example.mykku.feed.tool

import com.example.mykku.board.domain.Board
import com.example.mykku.exception.ErrorCode
import com.example.mykku.exception.MykkuException
import com.example.mykku.feed.domain.Feed
import com.example.mykku.feed.repository.FeedRepository
import com.example.mykku.member.domain.Member
import com.example.mykku.member.domain.SocialProvider
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ExtendWith(MockitoExtension::class)
class FeedReaderTest {

    @Mock
    private lateinit var feedRepository: FeedRepository

    @InjectMocks
    private lateinit var feedReader: FeedReader

    private fun createMockMember(): Member {
        return Member(
            id = "member123",
            nickname = "테스트유저",
            role = "USER",
            profileImage = "profile.jpg",
            provider = SocialProvider.GOOGLE,
            socialId = "12345",
            email = "test@example.com"
        )
    }

    private fun createMockBoard(): Board {
        return Board(
            id = 1L,
            title = "테스트보드",
            logo = "logo.jpg"
        )
    }

    private fun createMockFeed(id: Long): Feed {
        return Feed(
            id = id,
            title = "테스트 피드 $id",
            content = "테스트 내용",
            board = createMockBoard(),
            member = createMockMember()
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
    fun `getFeedsByFollower는 팔로우한 멤버들의 피드 목록을 반환한다`() {
        val member1 = createMockMember()
        val member2 = Member(
            id = "member456",
            nickname = "테스트유저2",
            role = "USER",
            profileImage = "profile2.jpg",
            provider = SocialProvider.KAKAO,
            socialId = "67890",
            email = "test2@example.com"
        )
        val members = listOf(member1, member2)
        
        val feed1 = createMockFeed(1L)
        val feed2 = createMockFeed(2L)
        val feeds = listOf(feed1, feed2)

        whenever(feedRepository.findAllByMemberIn(members)).thenReturn(feeds)

        val result = feedReader.getFeedsByFollower(members)

        assertEquals(2, result.size)
        assertEquals(feed1, result[0])
        assertEquals(feed2, result[1])
    }

    @Test
    fun `getFeedsByFollower는 팔로우한 멤버가 없을 때 빈 목록을 반환한다`() {
        val emptyMembers = emptyList<Member>()

        whenever(feedRepository.findAllByMemberIn(emptyMembers)).thenReturn(emptyList())

        val result = feedReader.getFeedsByFollower(emptyMembers)

        assertTrue(result.isEmpty())
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

        val exception = assertThrows<MykkuException> {
            feedReader.getFeedById(feedId)
        }

        assertEquals(ErrorCode.FEED_NOT_FOUND, exception.errorCode)
    }
}