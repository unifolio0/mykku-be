package com.example.mykku.board.domain

import com.example.mykku.exception.ErrorCode
import com.example.mykku.exception.MykkuException
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test

class BoardTest {
    @Test
    fun `Board의 title은 일정 길이 미만이어야 한다`() {
        assertThrows<MykkuException> {
            Board(title = "a".repeat(Board.TITLE_MAX_LENGTH + 1))
        }.apply {
            assert(this.errorCode == ErrorCode.BOARD_TITLE_TOO_LONG)
        }
    }
}
