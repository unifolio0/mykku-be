package com.example.mykku.like.tool

import com.example.mykku.BaseToolTest
import com.example.mykku.board.domain.Board
import com.example.mykku.feed.domain.Feed
import com.example.mykku.like.domain.LikeFeed
import com.example.mykku.like.repository.LikeFeedRepository
import com.example.mykku.member.domain.Member
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertSame

class LikeFeedWriterTest : BaseToolTest() {

    @Mock
    private lateinit var likeFeedRepository: LikeFeedRepository

    @InjectMocks
    private lateinit var likeFeedWriter: LikeFeedWriter

    private fun createMockFeed(): Feed {
        val board = createMockBoard()
        return Feed(
            id = 1L,
            title = "테스트 피드",
            content = "테스트 내용",
            board = board,
            member = createMockMember()
        )
    }

    @Test
    fun `createLikeFeed는 피드 좋아요를 생성하고 저장된 결과를 반환한다`() {
        val member = createMockMember()
        val feed = createMockFeed()
        val mockLikeFeed = LikeFeed(
            id = 1L,
            member = member,
            feed = feed
        )
        
        whenever(likeFeedRepository.save(any<LikeFeed>()))
            .thenReturn(mockLikeFeed)

        val result = likeFeedWriter.createLikeFeed(feed, member)

        assertSame(mockLikeFeed, result)
        assertEquals(member, result.member)
        assertEquals(feed, result.feed)
        verify(likeFeedRepository).save(any())
    }

    @Test
    fun `deleteLikeFeed는 memberId와 feedId로 피드 좋아요를 삭제한다`() {
        val memberId = "member123"
        val feedId = 1L

        likeFeedWriter.deleteLikeFeed(memberId, feedId)

        verify(likeFeedRepository).deleteByMemberIdAndFeedId(memberId, feedId)
    }
}