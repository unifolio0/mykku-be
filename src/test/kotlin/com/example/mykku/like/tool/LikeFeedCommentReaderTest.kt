package com.example.mykku.like.tool

import com.example.mykku.board.domain.Board
import com.example.mykku.exception.ErrorCode
import com.example.mykku.exception.MykkuException
import com.example.mykku.feed.domain.Feed
import com.example.mykku.feed.domain.FeedComment
import com.example.mykku.like.repository.LikeFeedCommentRepository
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
class LikeFeedCommentReaderTest {

    @Mock
    private lateinit var likeFeedCommentRepository: LikeFeedCommentRepository

    @InjectMocks
    private lateinit var likeFeedCommentReader: LikeFeedCommentReader

    private fun createMockFeedComment(): FeedComment {
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

        val exception = assertThrows<MykkuException> {
            likeFeedCommentReader.validateLikeFeedCommentNotExists(memberId, feedCommentId)
        }

        assertEquals(ErrorCode.LIKE_FEED_COMMENT_ALREADY_LIKED, exception.errorCode)
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

        val exception = assertThrows<MykkuException> {
            likeFeedCommentReader.validateLikeFeedCommentExists(memberId, feedCommentId)
        }

        assertEquals(ErrorCode.LIKE_FEED_COMMENT_NOT_FOUND, exception.errorCode)
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