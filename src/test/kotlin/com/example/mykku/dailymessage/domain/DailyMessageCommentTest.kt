package com.example.mykku.dailymessage.domain

import com.example.mykku.exception.ErrorCode
import com.example.mykku.exception.MykkuException
import com.example.mykku.member.domain.Member
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import kotlin.test.Test

class DailyMessageCommentTest {
    @Test
    fun `DailyMessageComment의 content는 일정 길이 미만이어야 한다`() {
        val dailyMessage: DailyMessage = mock()
        val member: Member = mock()

        assertThrows<MykkuException> {
            DailyMessageComment(
                content = "a".repeat(DailyMessageComment.CONTENT_MAX_LENGTH + 1),
                dailyMessage = dailyMessage,
                member = member
            )
        }.apply {
            assert(this.errorCode == ErrorCode.DAILY_MESSAGE_COMMENT_CONTENT_TOO_LONG)
        }
    }
}
