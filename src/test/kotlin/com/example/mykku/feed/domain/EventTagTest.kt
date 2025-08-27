package com.example.mykku.feed.domain

import com.example.mykku.exception.ErrorCode
import com.example.mykku.exception.MykkuException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime
import kotlin.test.assertEquals

class EventTagTest {

    private fun createTestEvent(): Event {
        return Event(
            isContest = false,
            title = "테스트 이벤트",
            expiredAt = LocalDateTime.now().plusDays(30)
        )
    }

    @Test
    fun `title이 최대 길이 20자일 때 성공한다`() {
        val event = createTestEvent()
        val validTitle = "r".repeat(EventTag.TITLE_MAX_LENGTH)
        
        EventTag(title = validTitle, event = event)
    }

    @Test
    fun `title이 최대 길이를 초과하면 예외가 발생한다`() {
        val event = createTestEvent()
        val invalidTitle = "r".repeat(EventTag.TITLE_MAX_LENGTH + 1)

        val exception = assertThrows<MykkuException> {
            EventTag(title = invalidTitle, event = event)
        }
        
        assertEquals(ErrorCode.TAG_TITLE_TOO_LONG, exception.errorCode)
    }

    @Test
    fun `유효한 패턴의 title로 생성할 수 있다`() {
        val event = createTestEvent()
        
        EventTag(title = "한글123", event = event)
        EventTag(title = "English123", event = event)
        EventTag(title = "123456", event = event)
    }

    @Test
    fun `특수문자가 포함된 title이면 예외가 발생한다`() {
        val event = createTestEvent()
        
        val exception = assertThrows<MykkuException> {
            EventTag(title = "태그!", event = event)
        }
        
        assertEquals(ErrorCode.TAG_INVALID_FORMAT, exception.errorCode)
    }

    @Test
    fun `공백이 포함된 title이면 예외가 발생한다`() {
        val event = createTestEvent()
        
        val exception = assertThrows<MykkuException> {
            EventTag(title = "태그 테스트", event = event)
        }
        
        assertEquals(ErrorCode.TAG_INVALID_FORMAT, exception.errorCode)
    }
}