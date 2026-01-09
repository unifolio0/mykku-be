package com.example.mykku.dailymessage.tool

import com.example.mykku.BaseToolTest
import com.example.mykku.dailymessage.domain.DailyMessage
import com.example.mykku.dailymessage.domain.SortDirection
import com.example.mykku.dailymessage.exception.DailyMessageErrorCode
import com.example.mykku.dailymessage.exception.DailyMessageException
import com.example.mykku.dailymessage.repository.DailyMessageRepository
import java.time.LocalDate
import java.util.Optional
import kotlin.test.assertEquals
import kotlin.test.assertSame
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.whenever
import org.springframework.data.domain.PageRequest

class DailyMessageReaderTest : BaseToolTest() {

    @Mock
    private lateinit var dailyMessageRepository: DailyMessageRepository

    @InjectMocks
    private lateinit var dailyMessageReader: DailyMessageReader

    private fun createMockDailyMessage(id: Long = 1L, date: LocalDate = LocalDate.now()): DailyMessage {
        return DailyMessage(
            id = id,
            title = "오늘의 메시지",
            content = "테스트 메시지",
            date = date
        )
    }

    @Test
    fun `getTodayDailyMessage는 오늘 날짜의 메시지가 없으면 예외를 발생시킨다`() {
        whenever(dailyMessageRepository.findByDate(LocalDate.now()))
            .thenReturn(null)

        val exception = assertThrows<DailyMessageException> {
            dailyMessageReader.getTodayDailyMessage()
        }

        assertEquals(DailyMessageErrorCode.DAILY_MESSAGE_NOT_FOUND, exception.errorCode)
    }

    @Test
    fun `getTodayDailyMessage는 오늘 날짜의 메시지가 있으면 메시지를 반환한다`() {
        val mockMessage = createMockDailyMessage()

        whenever(dailyMessageRepository.findByDate(LocalDate.now()))
            .thenReturn(mockMessage)

        val result = dailyMessageReader.getTodayDailyMessage()

        assertSame(mockMessage, result)
    }

    @Test
    fun `getDailyMessages는 내림차순 정렬로 메시지 목록을 반환한다`() {
        val date = LocalDate.of(2024, 1, 15)
        val limit = 10
        val mockMessages = listOf(createMockDailyMessage())

        val expectedPageable = PageRequest.of(0, limit)
        whenever(dailyMessageRepository.findByDateBeforeOrEqualOrderByDateDesc(date, expectedPageable))
            .thenReturn(mockMessages)

        val result = dailyMessageReader.getDailyMessages(date, limit, SortDirection.DESC)

        assertEquals(mockMessages, result)
    }

    @Test
    fun `getDailyMessages는 오름차순 정렬로 메시지 목록을 반환한다`() {
        val date = LocalDate.of(2024, 1, 15)
        val limit = 10
        val mockMessages = listOf(createMockDailyMessage())

        val expectedPageable = PageRequest.of(0, limit)
        whenever(dailyMessageRepository.findByDateBeforeOrEqualOrderByDateAsc(date, expectedPageable))
            .thenReturn(mockMessages)

        val result = dailyMessageReader.getDailyMessages(date, limit, SortDirection.ASC)

        assertEquals(mockMessages, result)
    }

    @Test
    fun `getDailyMessage는 존재하지 않는 ID로 조회하면 예외를 발생시킨다`() {
        val messageId = 999L

        whenever(dailyMessageRepository.findById(messageId))
            .thenReturn(Optional.empty())

        val exception = assertThrows<DailyMessageException> {
            dailyMessageReader.getDailyMessage(messageId)
        }

        assertEquals(DailyMessageErrorCode.DAILY_MESSAGE_NOT_FOUND, exception.errorCode)
    }

    @Test
    fun `getDailyMessage는 존재하는 ID로 조회하면 메시지를 반환한다`() {
        val messageId = 1L
        val mockMessage = createMockDailyMessage(messageId)

        whenever(dailyMessageRepository.findById(messageId))
            .thenReturn(Optional.of(mockMessage))

        val result = dailyMessageReader.getDailyMessage(messageId)

        assertSame(mockMessage, result)
    }
}
