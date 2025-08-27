package com.example.mykku.board.domain

import com.example.mykku.exception.ErrorCode
import com.example.mykku.exception.MykkuException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class BoardTest {

    @Test
    fun `title이 최대 길이 16자일 때 성공한다`() {
        val validTitle = "r".repeat(Board.TITLE_MAX_LENGTH)
        
        Board(title = validTitle, logo = "logo")
    }

    @Test
    fun `title이 최대 길이를 초과하면 예외가 발생한다`() {
        val invalidTitle = "r".repeat(Board.TITLE_MAX_LENGTH + 1)

        val exception = assertThrows<MykkuException> {
            Board(title = invalidTitle, logo = "logo")
        }
        
        assertEquals(ErrorCode.BOARD_TITLE_TOO_LONG, exception.errorCode)
    }
}