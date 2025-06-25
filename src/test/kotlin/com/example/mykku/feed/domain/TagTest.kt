package com.example.mykku.feed.domain

import com.example.mykku.exception.ErrorCode
import com.example.mykku.exception.MykkuException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class TagTest {
    @Test
    fun `Tag의 title은 일정 길이 미만이어야 한다`() {
        assertThrows<MykkuException> {
            BasicTag(title = "a".repeat(Tag.TITLE_MAX_LENGTH + 1))
        }.apply {
            assert(this.errorCode == ErrorCode.TAG_TITLE_TOO_LONG)
        }
    }

    @Test
    fun `Tag의 title은 한글, 영문, 숫자만 사용할 수 있다`() {
        assertThrows<MykkuException> {
            BasicTag(title = "태그!@#")
        }.apply {
            assert(this.errorCode == ErrorCode.TAG_INVALID_FORMAT)
        }
    }
}
