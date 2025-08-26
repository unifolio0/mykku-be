package com.example.mykku.like.tool

import com.example.mykku.board.domain.Board
import com.example.mykku.exception.ErrorCode
import com.example.mykku.exception.MykkuException
import com.example.mykku.feed.domain.Feed
import com.example.mykku.like.repository.LikeFeedRepository
import com.example.mykku.member.domain.Member
import com.example.mykku.member.domain.SocialProvider
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@ExtendWith(MockitoExtension::class)
class LikeFeedReaderTest {

    @Mock
    private lateinit var likeFeedRepository: LikeFeedRepository

    @InjectMocks
    private lateinit var likeFeedReader: LikeFeedReader

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

    @Test
    fun `isLiked는 좋아요가 존재하면 true를 반환한다`() {
        val memberId = "member123"
        val feed = createMockFeed()
        
        whenever(likeFeedRepository.existsByMemberIdAndFeed(memberId, feed))
            .thenReturn(true)

        val result = likeFeedReader.isLiked(memberId, feed)

        assertTrue(result)
    }

    @Test
    fun `isLiked는 좋아요가 존재하지 않으면 false를 반환한다`() {
        val memberId = "member123"
        val feed = createMockFeed()
        
        whenever(likeFeedRepository.existsByMemberIdAndFeed(memberId, feed))
            .thenReturn(false)

        val result = likeFeedReader.isLiked(memberId, feed)

        assertFalse(result)
    }

    @Test
    fun `validateLikeFeedNotExists는 이미 좋아요가 존재하면 예외를 발생시킨다`() {
        val memberId = "member123"
        val feedId = 1L
        
        whenever(likeFeedRepository.existsByMemberIdAndFeedId(memberId, feedId))
            .thenReturn(true)

        val exception = assertThrows<MykkuException> {
            likeFeedReader.validateLikeFeedNotExists(memberId, feedId)
        }

        assertEquals(ErrorCode.LIKE_FEED_ALREADY_LIKED, exception.errorCode)
    }

    @Test
    fun `validateLikeFeedNotExists는 좋아요가 존재하지 않으면 정상 처리된다`() {
        val memberId = "member123"
        val feedId = 1L
        
        whenever(likeFeedRepository.existsByMemberIdAndFeedId(memberId, feedId))
            .thenReturn(false)

        likeFeedReader.validateLikeFeedNotExists(memberId, feedId)
    }

    @Test
    fun `validateLikeFeedExists는 좋아요가 존재하지 않으면 예외를 발생시킨다`() {
        val memberId = "member123"
        val feedId = 1L
        
        whenever(likeFeedRepository.existsByMemberIdAndFeedId(memberId, feedId))
            .thenReturn(false)

        val exception = assertThrows<MykkuException> {
            likeFeedReader.validateLikeFeedExists(memberId, feedId)
        }

        assertEquals(ErrorCode.LIKE_FEED_NOT_FOUND, exception.errorCode)
    }

    @Test
    fun `validateLikeFeedExists는 좋아요가 존재하면 정상 처리된다`() {
        val memberId = "member123"
        val feedId = 1L
        
        whenever(likeFeedRepository.existsByMemberIdAndFeedId(memberId, feedId))
            .thenReturn(true)

        likeFeedReader.validateLikeFeedExists(memberId, feedId)
    }
}