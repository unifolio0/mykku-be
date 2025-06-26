package com.example.mykku.dailymessage.domain

import com.example.mykku.exception.ErrorCode
import com.example.mykku.exception.MykkuException
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate
import kotlin.test.Test

class DailyMessageTest {
    @Test
    fun `DailyMessage의 content는 일정 길이 미만이어야 한다`() {
        assertThrows<MykkuException> {
            DailyMessage(
                content = "a".repeat(DailyMessage.CONTENT_MAX_LENGTH + 1),
                date = LocalDate.now(),
                title = "title"
            )
        }.apply {
            assert(this.errorCode == ErrorCode.DAILY_MESSAGE_CONTENT_TOO_LONG)
        }
    }
}
