package com.example.mykku.like.tool

import com.example.mykku.BaseToolTest
import com.example.mykku.like.exception.LikeException
import com.example.mykku.like.exception.LikeErrorCode
import com.example.mykku.feed.domain.Feed
import com.example.mykku.feed.domain.FeedComment
import com.example.mykku.like.repository.LikeFeedCommentRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LikeFeedCommentReaderTest : BaseToolTest() {

    @Mock
    private lateinit var likeFeedCommentRepository: LikeFeedCommentRepository

    @InjectMocks
    private lateinit var likeFeedCommentReader: LikeFeedCommentReader

    private fun createMockFeedComment(): FeedComment {
        val member = createMockMember("test_member", "테스트유저")
        val board = createMockBoard()
        val feed = Feed(
            id = 1L,
            title = "테스트 피드",
            content = "테스트 내용",
            board = board,
            member = member
        )
        return FeedComment(
            id = 1L,
            content = "테스트 댓글",
            feed = feed,
            member = member
        )
    }

    @Test
    fun `validateLikeFeedCommentNotExists는 이미 좋아요가 존재하면 예외를 발생시킨다`() {
        val memberId = "member123"
        val feedCommentId = 1L
        
        whenever(likeFeedCommentRepository.existsByMemberIdAndFeedCommentId(memberId, feedCommentId))
            .thenReturn(true)

        val exception = assertThrows<LikeException> {
            likeFeedCommentReader.validateLikeFeedCommentNotExists(memberId, feedCommentId)
        }

        assertEquals(LikeErrorCode.LIKE_FEED_COMMENT_ALREADY_LIKED, exception.errorCode)
    }

    @Test
    fun `validateLikeFeedCommentNotExists는 좋아요가 존재하지 않으면 정상 처리된다`() {
        val memberId = "member123"
        val feedCommentId = 1L
        
        whenever(likeFeedCommentRepository.existsByMemberIdAndFeedCommentId(memberId, feedCommentId))
            .thenReturn(false)

        likeFeedCommentReader.validateLikeFeedCommentNotExists(memberId, feedCommentId)
    }

    @Test
    fun `validateLikeFeedCommentExists는 좋아요가 존재하지 않으면 예외를 발생시킨다`() {
        val memberId = "member123"
        val feedCommentId = 1L
        
        whenever(likeFeedCommentRepository.existsByMemberIdAndFeedCommentId(memberId, feedCommentId))
            .thenReturn(false)

        val exception = assertThrows<LikeException> {
            likeFeedCommentReader.validateLikeFeedCommentExists(memberId, feedCommentId)
        }

        assertEquals(LikeErrorCode.LIKE_FEED_COMMENT_NOT_FOUND, exception.errorCode)
    }

    @Test
    fun `validateLikeFeedCommentExists는 좋아요가 존재하면 정상 처리된다`() {
        val memberId = "member123"
        val feedCommentId = 1L
        
        whenever(likeFeedCommentRepository.existsByMemberIdAndFeedCommentId(memberId, feedCommentId))
            .thenReturn(true)

        likeFeedCommentReader.validateLikeFeedCommentExists(memberId, feedCommentId)
    }

    @Test
    fun `isLiked는 좋아요가 존재하면 true를 반환한다`() {
        val memberId = "member123"
        val feedComment = createMockFeedComment()
        
        whenever(likeFeedCommentRepository.existsByMemberIdAndFeedCommentId(memberId, feedComment.id!!))
            .thenReturn(true)

        val result = likeFeedCommentReader.isLiked(memberId, feedComment)

        assertTrue(result)
    }

    @Test
    fun `isLiked는 좋아요가 존재하지 않으면 false를 반환한다`() {
        val memberId = "member123"
        val feedComment = createMockFeedComment()
        
        whenever(likeFeedCommentRepository.existsByMemberIdAndFeedCommentId(memberId, feedComment.id!!))
            .thenReturn(false)

        val result = likeFeedCommentReader.isLiked(memberId, feedComment)

        assertFalse(result)
    }
}