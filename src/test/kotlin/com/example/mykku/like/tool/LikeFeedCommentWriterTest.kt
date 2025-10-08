package com.example.mykku.like.tool

import com.example.mykku.BaseToolTest
import com.example.mykku.feed.domain.Feed
import com.example.mykku.feed.domain.FeedComment
import com.example.mykku.like.domain.LikeFeedComment
import com.example.mykku.like.repository.LikeFeedCommentRepository
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertSame

class LikeFeedCommentWriterTest : BaseToolTest() {

    @Mock
    private lateinit var likeFeedCommentRepository: LikeFeedCommentRepository

    @InjectMocks
    private lateinit var likeFeedCommentWriter: LikeFeedCommentWriter

    private fun createMockFeedComment(): FeedComment {
        val board = createMockBoard()
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