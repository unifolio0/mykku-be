package com.example.mykku.dailymessage

import com.example.mykku.dailymessage.domain.DailyMessage
import com.example.mykku.dailymessage.domain.DailyMessageComment
import com.example.mykku.dailymessage.dto.CreateCommentRequest
import com.example.mykku.dailymessage.dto.UpdateCommentRequest
import com.example.mykku.dailymessage.tool.DailyMessageCommentReader
import com.example.mykku.dailymessage.tool.DailyMessageCommentWriter
import com.example.mykku.dailymessage.tool.DailyMessageReader
import com.example.mykku.exception.ErrorCode
import com.example.mykku.exception.MykkuException
import com.example.mykku.member.domain.Member
import com.example.mykku.member.domain.SocialProvider
import com.example.mykku.member.tool.MemberReader
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class DailyMessageCommentServiceTest {

    @Mock
    private lateinit var dailyMessageReader: DailyMessageReader

    @Mock
    private lateinit var dailyMessageCommentReader: DailyMessageCommentReader

    @Mock
    private lateinit var dailyMessageCommentWriter: DailyMessageCommentWriter

    @Mock
    private lateinit var memberReader: MemberReader

    @InjectMocks
    private lateinit var dailyMessageCommentService: DailyMessageCommentService

    private fun createTestMember(id: String = "member1", nickname: String = "testUser"): Member {
        return Member(
            id = id,
            nickname = nickname,
            role = "USER",
            profileImage = "",
            provider = SocialProvider.GOOGLE,
            socialId = "123",
            email = "test@test.com"
        )
    }

    private fun createTestDailyMessage(id: Long = 1L): DailyMessage {
        return DailyMessage(
            id = id,
            title = "오늘의 메시지",
            content = "좋은 하루 되세요",
            date = LocalDate.now()
        )
    }

    private fun createTestComment(
        id: Long = 1L,
        content: String = "테스트 댓글",
        member: Member,
        dailyMessage: DailyMessage,
        parentComment: DailyMessageComment? = null,
        createdAt: LocalDateTime = LocalDateTime.now(),
        updatedAt: LocalDateTime = LocalDateTime.now()
    ): DailyMessageComment {
        val comment = DailyMessageComment(
            id = id,
            content = content,
            dailyMessage = dailyMessage,
            member = member,
            parentComment = parentComment
        )
        // Initialize BaseEntity fields - no choice but to use reflection since there are no setters
        val createdAtField = comment::class.java.superclass.getDeclaredField("createdAt")
        createdAtField.isAccessible = true
        createdAtField.set(comment, createdAt)
        
        val updatedAtField = comment::class.java.superclass.getDeclaredField("updatedAt")
        updatedAtField.isAccessible = true
        updatedAtField.set(comment, updatedAt)
        
        return comment
    }

    @Test
    fun `createComment - 댓글을 정상적으로 생성한다`() {
        // given
        val member = createTestMember()
        val dailyMessage = createTestDailyMessage()
        val request = CreateCommentRequest(content = "좋은 글이네요", parentCommentId = null)
        val comment = createTestComment(
            content = request.content,
            member = member,
            dailyMessage = dailyMessage
        )

        whenever(dailyMessageReader.getDailyMessage(1L)).thenReturn(dailyMessage)
        whenever(memberReader.getMemberById("member1")).thenReturn(member)
        whenever(dailyMessageCommentWriter.createComment(
            content = request.content,
            dailyMessage = dailyMessage,
            member = member,
            parentComment = null
        )).thenReturn(comment)

        // when
        val result = dailyMessageCommentService.createComment(1L, "member1", request)

        // then
        assertEquals(comment.id, result.id)
        assertEquals(comment.content, result.content)
        assertEquals(comment.member.nickname, result.memberName)
    }

    @Test
    fun `createComment - 부모 댓글이 있는 댓글을 생성한다`() {
        // given
        val member = createTestMember()
        val dailyMessage = createTestDailyMessage()
        val parentComment = createTestComment(
            id = 2L,
            content = "부모 댓글",
            member = member,
            dailyMessage = dailyMessage
        )
        
        val request = CreateCommentRequest(content = "대댓글입니다", parentCommentId = 2L)
        val comment = createTestComment(
            id = 3L,
            content = request.content,
            member = member,
            dailyMessage = dailyMessage,
            parentComment = parentComment
        )

        whenever(dailyMessageReader.getDailyMessage(1L)).thenReturn(dailyMessage)
        whenever(memberReader.getMemberById("member1")).thenReturn(member)
        whenever(dailyMessageCommentReader.getCommentByDailyMessageId(2L, 1L)).thenReturn(parentComment)
        whenever(dailyMessageCommentWriter.createComment(
            content = request.content,
            dailyMessage = dailyMessage,
            member = member,
            parentComment = parentComment
        )).thenReturn(comment)

        // when
        val result = dailyMessageCommentService.createComment(1L, "member1", request)

        // then
        assertEquals(comment.id, result.id)
        assertEquals(comment.content, result.content)
    }

    @Test
    fun `updateComment - 댓글을 정상적으로 수정한다`() {
        // given
        val member = createTestMember()
        val dailyMessage = createTestDailyMessage()
        val request = UpdateCommentRequest(content = "수정된 댓글")
        val comment = createTestComment(
            content = "원본 댓글",
            member = member,
            dailyMessage = dailyMessage
        )
        
        val updatedComment = createTestComment(
            content = request.content,
            member = member,
            dailyMessage = dailyMessage
        )

        whenever(dailyMessageCommentReader.getComment(1L)).thenReturn(comment)
        whenever(dailyMessageCommentWriter.updateComment(
            comment = comment,
            newContent = request.content
        )).thenReturn(updatedComment)

        // when
        val result = dailyMessageCommentService.updateComment(1L, "member1", request)

        // then
        assertEquals(updatedComment.content, result.content)
    }

    @Test
    fun `updateComment - 작성자가 아닌 사용자가 수정을 시도하면 예외가 발생한다`() {
        // given
        val member = createTestMember()
        val otherMember = createTestMember(id = "member2", nickname = "otherUser")
        val dailyMessage = createTestDailyMessage()
        
        val request = UpdateCommentRequest(content = "수정된 댓글")
        val comment = createTestComment(
            content = "원본 댓글",
            member = otherMember,
            dailyMessage = dailyMessage
        )

        whenever(dailyMessageCommentReader.getComment(1L)).thenReturn(comment)

        // when & then
        val exception = assertThrows<MykkuException> {
            dailyMessageCommentService.updateComment(1L, "member1", request)
        }
        assertEquals(ErrorCode.COMMENT_FORBIDDEN_ACCESS, exception.errorCode)
    }
}