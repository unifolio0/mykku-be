package com.example.mykku.dailymessage.tool

import com.example.mykku.dailymessage.domain.DailyMessage
import com.example.mykku.dailymessage.domain.DailyMessageComment
import com.example.mykku.dailymessage.repository.DailyMessageCommentRepository
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
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame

@ExtendWith(MockitoExtension::class)
class DailyMessageCommentWriterTest {

    @Mock
    private lateinit var dailyMessageCommentRepository: DailyMessageCommentRepository

    @InjectMocks
    private lateinit var dailyMessageCommentWriter: DailyMessageCommentWriter

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

    private fun createMockDailyMessage(): DailyMessage {
        return DailyMessage(
            id = 1L,
            title = "오늘의 메시지 제목",
            content = "오늘의 메시지",
            date = LocalDate.now()
        )
    }

    @Test
    fun `createComment는 새로운 댓글을 생성하고 저장된 결과를 반환한다`() {
        val content = "테스트 댓글"
        val dailyMessage = createMockDailyMessage()
        val member = createMockMember()
        val mockComment = DailyMessageComment(
            id = 1L,
            content = content,
            dailyMessage = dailyMessage,
            member = member,
            parentComment = null
        )

        whenever(dailyMessageCommentRepository.save(any<DailyMessageComment>()))
            .thenReturn(mockComment)

        val result = dailyMessageCommentWriter.createComment(content, dailyMessage, member)

        assertSame(mockComment, result)
        assertEquals(content, result.content)
        assertEquals(dailyMessage, result.dailyMessage)
        assertEquals(member, result.member)
        assertNull(result.parentComment)
        verify(dailyMessageCommentRepository).save(any())
    }

    @Test
    fun `createComment는 부모 댓글이 있는 대댓글을 생성하고 저장된 결과를 반환한다`() {
        val content = "대댓글 내용"
        val dailyMessage = createMockDailyMessage()
        val member = createMockMember()
        val parentComment = DailyMessageComment(
            id = 1L,
            content = "부모 댓글",
            dailyMessage = dailyMessage,
            member = member,
            parentComment = null
        )
        val mockChildComment = DailyMessageComment(
            id = 2L,
            content = content,
            dailyMessage = dailyMessage,
            member = member,
            parentComment = parentComment
        )

        whenever(dailyMessageCommentRepository.save(any<DailyMessageComment>()))
            .thenReturn(mockChildComment)

        val result = dailyMessageCommentWriter.createComment(content, dailyMessage, member, parentComment)

        assertSame(mockChildComment, result)
        assertEquals(content, result.content)
        assertEquals(dailyMessage, result.dailyMessage)
        assertEquals(member, result.member)
        assertEquals(parentComment, result.parentComment)
        verify(dailyMessageCommentRepository).save(any())
    }

    @Test
    fun `updateComment는 댓글 내용을 업데이트하고 저장된 결과를 반환한다`() {
        val originalContent = "원래 내용"
        val newContent = "수정된 내용"
        val dailyMessage = createMockDailyMessage()
        val member = createMockMember()
        val comment = DailyMessageComment(
            id = 1L,
            content = originalContent,
            dailyMessage = dailyMessage,
            member = member,
            parentComment = null
        )
        val updatedComment = DailyMessageComment(
            id = 1L,
            content = newContent,
            dailyMessage = dailyMessage,
            member = member,
            parentComment = null
        )

        whenever(dailyMessageCommentRepository.save(comment)).thenReturn(updatedComment)

        val result = dailyMessageCommentWriter.updateComment(comment, newContent)

        assertSame(updatedComment, result)
        verify(dailyMessageCommentRepository).save(comment)
    }
}