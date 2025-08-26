package com.example.mykku.dailymessage.domain

import com.example.mykku.exception.ErrorCode
import com.example.mykku.exception.MykkuException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate
import kotlin.test.assertEquals

class DailyMessageTest {

    @Test
    fun `content가 최대 길이 42자일 때 성공한다`() {
        val validContent = "r".repeat(DailyMessage.CONTENT_MAX_LENGTH)
        
        DailyMessage(
            title = "오늘의 메시지",
            content = validContent,
            date = LocalDate.now()
        )
    }

    @Test
    fun `content가 최대 길이를 초과하면 예외가 발생한다`() {
        val invalidContent = "r".repeat(DailyMessage.CONTENT_MAX_LENGTH + 1)

        val exception = assertThrows<MykkuException> {
            DailyMessage(
                title = "오늘의 메시지",
                content = invalidContent,
                date = LocalDate.now()
            )
        }
        
        assertEquals(ErrorCode.DAILY_MESSAGE_CONTENT_TOO_LONG, exception.errorCode)
    }
}