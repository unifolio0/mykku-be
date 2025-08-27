package com.example.mykku.dailymessage.tool

import com.example.mykku.dailymessage.domain.DailyMessage
import com.example.mykku.dailymessage.domain.DailyMessageComment
import com.example.mykku.dailymessage.repository.DailyMessageCommentRepository
import com.example.mykku.exception.ErrorCode
import com.example.mykku.exception.MykkuException
import com.example.mykku.member.domain.Member
import com.example.mykku.member.domain.SocialProvider
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import java.time.LocalDate
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertSame

@ExtendWith(MockitoExtension::class)
class DailyMessageCommentReaderTest {

    @Mock
    private lateinit var dailyMessageCommentRepository: DailyMessageCommentRepository

    @InjectMocks
    private lateinit var dailyMessageCommentReader: DailyMessageCommentReader

    private fun createMockComment(id: Long = 1L): DailyMessageComment {
        val member = Member(
            id = "test_member",
            nickname = "테스트유저",
            role = "USER",
            profileImage = "profile.jpg",
            provider = SocialProvider.GOOGLE,
            socialId = "12345",
            email = "test@example.com"
        )
        
        val dailyMessage = DailyMessage(
            id = 1L,
            title = "오늘의 메시지",
            content = "테스트 메시지",
            date = LocalDate.now()
        )
        
        return DailyMessageComment(
            id = id,
            content = "테스트 댓글",
            dailyMessage = dailyMessage,
            member = member
        )
    }

    @Test
    fun `getComment는 존재하지 않는 댓글 ID로 조회하면 예외를 발생시킨다`() {
        val commentId = 999L
        
        whenever(dailyMessageCommentRepository.findById(commentId))
            .thenReturn(Optional.empty())

        val exception = assertThrows<MykkuException> {
            dailyMessageCommentReader.getComment(commentId)
        }

        assertEquals(ErrorCode.DAILY_MESSAGE_COMMENT_NOT_FOUND, exception.errorCode)
    }

    @Test
    fun `getComment는 존재하는 댓글 ID로 조회하면 댓글을 반환한다`() {
        val commentId = 1L
        val mockComment = createMockComment(commentId)
        
        whenever(dailyMessageCommentRepository.findById(commentId))
            .thenReturn(Optional.of(mockComment))

        val result = dailyMessageCommentReader.getComment(commentId)

        assertSame(mockComment, result)
    }

    @Test
    fun `getCommentByDailyMessageId는 존재하지 않는 댓글이면 예외를 발생시킨다`() {
        val commentId = 1L
        val dailyMessageId = 1L
        
        whenever(dailyMessageCommentRepository.findByIdAndDailyMessageId(commentId, dailyMessageId))
            .thenReturn(null)

        val exception = assertThrows<MykkuException> {
            dailyMessageCommentReader.getCommentByDailyMessageId(commentId, dailyMessageId)
        }

        assertEquals(ErrorCode.DAILY_MESSAGE_COMMENT_NOT_FOUND, exception.errorCode)
    }

    @Test
    fun `getCommentByDailyMessageId는 존재하는 댓글이면 댓글을 반환한다`() {
        val commentId = 1L
        val dailyMessageId = 1L
        val mockComment = createMockComment(commentId)
        
        whenever(dailyMessageCommentRepository.findByIdAndDailyMessageId(commentId, dailyMessageId))
            .thenReturn(mockComment)

        val result = dailyMessageCommentReader.getCommentByDailyMessageId(commentId, dailyMessageId)

        assertSame(mockComment, result)
    }

    @Test
    fun `getDailyMessageCommentById는 존재하지 않는 댓글 ID로 조회하면 예외를 발생시킨다`() {
        val commentId = 999L
        
        whenever(dailyMessageCommentRepository.findById(commentId))
            .thenReturn(Optional.empty())

        val exception = assertThrows<MykkuException> {
            dailyMessageCommentReader.getDailyMessageCommentById(commentId)
        }

        assertEquals(ErrorCode.DAILY_MESSAGE_COMMENT_NOT_FOUND, exception.errorCode)
    }

    @Test
    fun `getDailyMessageCommentById는 존재하는 댓글 ID로 조회하면 댓글을 반환한다`() {
        val commentId = 1L
        val mockComment = createMockComment(commentId)
        
        whenever(dailyMessageCommentRepository.findById(commentId))
            .thenReturn(Optional.of(mockComment))

        val result = dailyMessageCommentReader.getDailyMessageCommentById(commentId)

        assertSame(mockComment, result)
    }
}