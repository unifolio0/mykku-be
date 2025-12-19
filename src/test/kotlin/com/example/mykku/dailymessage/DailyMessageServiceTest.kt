package com.example.mykku.dailymessage

import com.example.mykku.BaseServiceTest
import com.example.mykku.dailymessage.domain.DailyMessage
import com.example.mykku.dailymessage.domain.DailyMessageComment
import com.example.mykku.dailymessage.domain.SortDirection
import com.example.mykku.dailymessage.application.port.out.DailyMessageQueryPort
import com.example.mykku.member.domain.Member
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.whenever
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.test.assertEquals

class DailyMessageServiceTest : BaseServiceTest() {

    @Mock
    private lateinit var dailyMessageQueryPort: DailyMessageQueryPort

    @InjectMocks
    private lateinit var dailyMessageService: DailyMessageService

    private fun createTestDailyMessage(
        id: Long = 1L,
        title: String,
        content: String,
        date: LocalDate
    ): DailyMessage {
        val dailyMessage = DailyMessage(
            id = id,
            title = title,
            content = content,
            date = date
        )
        initializeBaseEntityFieldsFromSuperclass(dailyMessage)
        return dailyMessage
    }

    private fun createTestComment(
        id: Long = 1L,
        content: String,
        dailyMessage: DailyMessage,
        member: Member,
        parentComment: DailyMessageComment? = null
    ): DailyMessageComment {
        val comment = DailyMessageComment(
            id = id,
            content = content,
            dailyMessage = dailyMessage,
            member = member,
            parentComment = parentComment
        )
        initializeBaseEntityFieldsFromSuperclass(comment)
        return comment
    }

    private val member = createTestMember(id = "member1", nickname = "testUser", email = "test@test.com")

    @Test
    fun `getDailyMessages - 일일 메시지 목록을 반환한다`() {
        // given
        val date = LocalDate.now()
        val limit = 10
        val sort = SortDirection.DESC
        
        val dailyMessage = createTestDailyMessage(
            id = 1L,
            title = "오늘의 메시지",
            content = "좋은 하루 되세요",
            date = date
        )
        
        val dailyMessages = listOf(dailyMessage)

        whenever(dailyMessageQueryPort.getDailyMessages(date, limit, sort)).thenReturn(dailyMessages)

        // when
        val result = dailyMessageService.getDailyMessages(date, limit, sort)

        // then
        assertEquals(1, result.size)
        assertEquals(dailyMessage.id, result[0].id)
        assertEquals(dailyMessage.title, result[0].title)
        assertEquals(dailyMessage.content, result[0].content)
        assertEquals(dailyMessage.date, result[0].date)
    }

    @Test
    fun `getDailyMessage - 특정 일일 메시지와 댓글을 반환한다`() {
        // given
        val dailyMessage = createTestDailyMessage(
            id = 1L,
            title = "오늘의 메시지",
            content = "좋은 하루 되세요",
            date = LocalDate.now()
        )

        val parentComment = createTestComment(
            id = 2L,
            content = "좋은 글이네요",
            dailyMessage = dailyMessage,
            member = member,
            parentComment = null
        )

        val replyComment = createTestComment(
            id = 3L,
            content = "동감합니다",
            dailyMessage = dailyMessage,
            member = member,
            parentComment = parentComment
        )

        val allComments = listOf(parentComment, replyComment)

        whenever(dailyMessageQueryPort.getDailyMessage(1L)).thenReturn(dailyMessage)
        whenever(dailyMessageQueryPort.getCommentsByDailyMessage(dailyMessage)).thenReturn(allComments)

        // when
        val result = dailyMessageService.getDailyMessage(1L)

        // then
        assertEquals(dailyMessage.id, result.id)
        assertEquals(dailyMessage.title, result.title)
        assertEquals(dailyMessage.content, result.content)
        assertEquals(1, result.comments.size)
        assertEquals(1, result.comments[0].replies.size)
    }

    @Test
    fun `getDailyMessage - 댓글이 없는 일일 메시지를 반환한다`() {
        // given
        val dailyMessage = createTestDailyMessage(
            id = 1L,
            title = "오늘의 메시지",
            content = "좋은 하루 되세요",
            date = LocalDate.now()
        )

        whenever(dailyMessageQueryPort.getDailyMessage(1L)).thenReturn(dailyMessage)
        whenever(dailyMessageQueryPort.getCommentsByDailyMessage(dailyMessage)).thenReturn(emptyList())

        // when
        val result = dailyMessageService.getDailyMessage(1L)

        // then
        assertEquals(dailyMessage.id, result.id)
        assertEquals(dailyMessage.title, result.title)
        assertEquals(dailyMessage.content, result.content)
        assertEquals(0, result.comments.size)
    }
}