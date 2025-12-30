package com.example.mykku.feed.tool

import com.example.mykku.BaseToolTest
import com.example.mykku.feed.domain.Feed
import com.example.mykku.feed.domain.FeedComment
import com.example.mykku.feed.repository.FeedCommentRepository
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals

class FeedCommentWriterTest : BaseToolTest() {

    @Mock
    private lateinit var feedCommentRepository: FeedCommentRepository

    @InjectMocks
    private lateinit var feedCommentWriter: FeedCommentWriter

    @Test
    fun `createComment는 댓글을 생성하고 저장한다`() {
        val member = createMockMember("member123", "테스트유저")
        val board = createMockBoard()
        val feed = Feed(
            id = 1L,
            title = "테스트 피드",
            content = "테스트 내용",
            board = board,
            member = member
        )

        val savedComment = FeedComment(
            id = 1L,
            content = "테스트 댓글",
            feed = feed,
            member = member
        )
        initializeBaseEntityFields(savedComment)

        whenever(feedCommentRepository.save(any<FeedComment>())).thenReturn(savedComment)

        val result = feedCommentWriter.createComment("테스트 댓글", feed, member, null)

        assertEquals(savedComment, result)
        verify(feedCommentRepository).save(any<FeedComment>())
    }

    @Test
    fun `createComment는 답글을 생성할 수 있다`() {
        val member = createMockMember("member123", "테스트유저")
        val board = createMockBoard()
        val feed = Feed(
            id = 1L,
            title = "테스트 피드",
            content = "테스트 내용",
            board = board,
            member = member
        )
        val parentComment = FeedComment(
            id = 1L,
            content = "부모 댓글",
            feed = feed,
            member = member
        )
        initializeBaseEntityFields(parentComment)

        val savedReply = FeedComment(
            id = 2L,
            content = "답글",
            feed = feed,
            member = member,
            parentComment = parentComment
        )
        initializeBaseEntityFields(savedReply, id = 2L)

        whenever(feedCommentRepository.save(any<FeedComment>())).thenReturn(savedReply)

        val result = feedCommentWriter.createComment("답글", feed, member, parentComment)

        assertEquals(savedReply, result)
        assertEquals(parentComment, result.parentComment)
    }

    @Test
    fun `updateComment는 댓글 내용을 수정하고 저장한다`() {
        val member = createMockMember("member123", "테스트유저")
        val board = createMockBoard()
        val feed = Feed(
            id = 1L,
            title = "테스트 피드",
            content = "테스트 내용",
            board = board,
            member = member
        )
        val comment = FeedComment(
            id = 1L,
            content = "원본 댓글",
            feed = feed,
            member = member
        )
        initializeBaseEntityFields(comment)

        whenever(feedCommentRepository.save(any<FeedComment>())).thenReturn(comment)

        val result = feedCommentWriter.updateComment(comment, "수정된 댓글")

        assertEquals("수정된 댓글", result.content)
        verify(feedCommentRepository).save(comment)
    }

    @Test
    fun `deleteComment는 댓글을 삭제한다`() {
        val member = createMockMember("member123", "테스트유저")
        val board = createMockBoard()
        val feed = Feed(
            id = 1L,
            title = "테스트 피드",
            content = "테스트 내용",
            board = board,
            member = member
        )
        val comment = FeedComment(
            id = 1L,
            content = "삭제할 댓글",
            feed = feed,
            member = member
        )
        initializeBaseEntityFields(comment)

        feedCommentWriter.deleteComment(comment)

        verify(feedCommentRepository).delete(comment)
    }

    @Test
    fun `deleteAllByFeed는 피드의 모든 댓글을 삭제한다`() {
        val member = createMockMember("member123", "테스트유저")
        val board = createMockBoard()
        val feed = Feed(
            id = 1L,
            title = "테스트 피드",
            content = "테스트 내용",
            board = board,
            member = member
        )

        feedCommentWriter.deleteAllByFeed(feed)

        verify(feedCommentRepository).deleteAllByFeed(feed)
    }
}
