package com.example.mykku.like.tool

import com.example.mykku.BaseToolTest
import com.example.mykku.like.exception.LikeException
import com.example.mykku.like.exception.LikeErrorCode
import com.example.mykku.feed.domain.Feed
import com.example.mykku.like.repository.LikeFeedRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LikeFeedReaderTest : BaseToolTest() {

    @Mock
    private lateinit var likeFeedRepository: LikeFeedRepository

    @InjectMocks
    private lateinit var likeFeedReader: LikeFeedReader

    private fun createMockFeed(): Feed {
        val member = createMockMember("test_member", "테스트유저")
        val board = createMockBoard()
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

        val exception = assertThrows<LikeException> {
            likeFeedReader.validateLikeFeedNotExists(memberId, feedId)
        }

        assertEquals(LikeErrorCode.LIKE_FEED_ALREADY_LIKED, exception.errorCode)
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

        val exception = assertThrows<LikeException> {
            likeFeedReader.validateLikeFeedExists(memberId, feedId)
        }

        assertEquals(LikeErrorCode.LIKE_FEED_NOT_FOUND, exception.errorCode)
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