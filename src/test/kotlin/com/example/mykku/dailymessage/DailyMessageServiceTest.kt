package com.example.mykku.dailymessage

import com.example.mykku.BaseServiceTest
import com.example.mykku.dailymessage.domain.DailyMessage
import com.example.mykku.dailymessage.tool.DailyMessageReader
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.time.LocalDate
import kotlin.test.assertEquals

class DailyMessageServiceTest : BaseServiceTest() {

    @Mock
    private lateinit var dailyMessageReader: DailyMessageReader

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

    @Test
    fun `getDailyMessages - 일일 메시지 목록을 페이지네이션으로 반환한다`() {
        // given
        val date = LocalDate.now()
        val pageable = PageRequest.of(0, 20)

        val dailyMessage = createTestDailyMessage(
            id = 1L,
            title = "오늘의 메시지",
            content = "좋은 하루 되세요",
            date = date
        )

        val dailyMessagesPage = PageImpl(listOf(dailyMessage), pageable, 1)

        whenever(dailyMessageReader.getDailyMessagesWithPagination(any(), any())).thenReturn(dailyMessagesPage)

        // when
        val result = dailyMessageService.getDailyMessages(date, pageable)

        // then
        assertEquals(1, result.totalElements)
        assertEquals(dailyMessage.id, result.content[0].id)
        assertEquals(dailyMessage.title, result.content[0].title)
        assertEquals(dailyMessage.content, result.content[0].content)
        assertEquals(dailyMessage.date, result.content[0].date)
    }

    @Test
    fun `getDailyMessage - 특정 일일 메시지를 반환한다`() {
        // given
        val dailyMessage = createTestDailyMessage(
            id = 1L,
            title = "오늘의 메시지",
            content = "좋은 하루 되세요",
            date = LocalDate.now()
        )

        whenever(dailyMessageReader.getDailyMessage(1L)).thenReturn(dailyMessage)

        // when
        val result = dailyMessageService.getDailyMessage(1L)

        // then
        assertEquals(dailyMessage.id, result.id)
        assertEquals(dailyMessage.title, result.title)
        assertEquals(dailyMessage.content, result.content)
    }
}
