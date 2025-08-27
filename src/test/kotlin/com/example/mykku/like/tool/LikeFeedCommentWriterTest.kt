package com.example.mykku.like.tool

import com.example.mykku.board.domain.Board
import com.example.mykku.feed.domain.Feed
import com.example.mykku.feed.domain.FeedComment
import com.example.mykku.like.domain.LikeFeedComment
import com.example.mykku.like.repository.LikeFeedCommentRepository
import com.example.mykku.member.domain.Member
import com.example.mykku.member.domain.SocialProvider
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertSame

@ExtendWith(MockitoExtension::class)
class LikeFeedCommentWriterTest {

    @Mock
    private lateinit var likeFeedCommentRepository: LikeFeedCommentRepository

    @InjectMocks
    private lateinit var likeFeedCommentWriter: LikeFeedCommentWriter

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

    private fun createMockFeedComment(): FeedComment {
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
            member = createMockMember()
        )
        
        return FeedComment(
            id = 1L,
            content = "테스트 댓글",
            feed = feed,
            member = createMockMember()
        )
    }

    @Test
    fun `createLikeFeedComment는 피드 댓글 좋아요를 생성하고 저장된 결과를 반환한다`() {
        val member = createMockMember()
        val feedComment = createMockFeedComment()
        val mockLikeFeedComment = LikeFeedComment(
            id = 1L,
            member = member,
            feedComment = feedComment
        )
        
        whenever(likeFeedCommentRepository.save(any<LikeFeedComment>()))
            .thenReturn(mockLikeFeedComment)

        val result = likeFeedCommentWriter.createLikeFeedComment(feedComment, member)

        assertSame(mockLikeFeedComment, result)
        assertEquals(member, result.member)
        assertEquals(feedComment, result.feedComment)
        verify(likeFeedCommentRepository).save(any())
    }

    @Test
    fun `deleteLikeFeedComment는 memberId와 feedCommentId로 피드 댓글 좋아요를 삭제한다`() {
        val memberId = "member123"
        val feedCommentId = 1L

        likeFeedCommentWriter.deleteLikeFeedComment(memberId, feedCommentId)

        verify(likeFeedCommentRepository).deleteByMemberIdAndFeedCommentId(memberId, feedCommentId)
    }
}