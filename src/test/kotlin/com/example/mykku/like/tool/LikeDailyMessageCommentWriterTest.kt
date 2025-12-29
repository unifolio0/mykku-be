package com.example.mykku.like.tool

import com.example.mykku.BaseToolTest
import com.example.mykku.dailymessage.domain.DailyMessage
import com.example.mykku.dailymessage.domain.DailyMessageComment
import com.example.mykku.like.domain.LikeDailyMessageComment
import com.example.mykku.like.repository.LikeDailyMessageCommentRepository
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertSame

class LikeDailyMessageCommentWriterTest : BaseToolTest() {

    @Mock
    private lateinit var likeDailyMessageCommentRepository: LikeDailyMessageCommentRepository

    @InjectMocks
    private lateinit var likeDailyMessageCommentWriter: LikeDailyMessageCommentWriter

    private fun createMockDailyMessageComment(): DailyMessageComment {
        val dailyMessage = DailyMessage(
            id = 1L,
            title = "오늘의 메시지",
            content = "테스트 메시지",
            date = LocalDate.now()
        )
        return DailyMessageComment(
            id = 1L,
            content = "테스트 댓글",
            dailyMessage = dailyMessage,
            member = createMockMember()
        )
    }

    @Test
    fun `createLikeDailyMessageComment는 좋아요를 생성하고 저장된 결과를 반환한다`() {
        val member = createMockMember()
        val dailyMessageComment = createMockDailyMessageComment()
        val mockLike = LikeDailyMessageComment(
            id = 1L,
            member = member,
            dailyMessageComment = dailyMessageComment
        )
        
        whenever(likeDailyMessageCommentRepository.save(any<LikeDailyMessageComment>()))
            .thenReturn(mockLike)

        val result = likeDailyMessageCommentWriter.createLikeDailyMessageComment(dailyMessageComment, member)

        assertSame(mockLike, result)
        assertEquals(member, result.member)
        assertEquals(dailyMessageComment, result.dailyMessageComment)
        verify(likeDailyMessageCommentRepository).save(any())
    }

    @Test
    fun `deleteLikeDailyMessageComment는 memberId와 dailyMessageCommentId로 좋아요를 삭제한다`() {
        val memberId = "member123"
        val dailyMessageCommentId = 1L

        likeDailyMessageCommentWriter.deleteLikeDailyMessageComment(memberId, dailyMessageCommentId)

        verify(likeDailyMessageCommentRepository)
            .deleteByMemberIdAndDailyMessageCommentId(memberId, dailyMessageCommentId)
    }
}