package com.example.mykku.board.tool

import com.example.mykku.BaseToolTest
import com.example.mykku.board.domain.Board
import com.example.mykku.board.exception.BoardErrorCode
import com.example.mykku.board.exception.BoardException
import com.example.mykku.board.repository.BoardRepository
import java.util.Optional
import kotlin.test.assertEquals
import kotlin.test.assertSame
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.whenever

class BoardReaderTest : BaseToolTest() {

    @Mock
    private lateinit var boardRepository: BoardRepository

    @InjectMocks
    private lateinit var boardReader: BoardReader

    @Test
    fun `validateDuplicateTitle은 중복된 제목이 존재하면 예외를 발생시킨다`() {
        val title = "중복된 제목"

        whenever(boardRepository.existsByTitle(title))
            .thenReturn(true)

        val exception = assertThrows<BoardException> {
            boardReader.validateDuplicateTitle(title)
        }

        assertEquals(BoardErrorCode.BOARD_DUPLICATE_TITLE, exception.errorCode)
    }

    @Test
    fun `validateDuplicateTitle은 중복된 제목이 존재하지 않으면 정상 처리된다`() {
        val title = "고유한 제목"

        whenever(boardRepository.existsByTitle(title))
            .thenReturn(false)

        boardReader.validateDuplicateTitle(title)
    }

    @Test
    fun `getBoardById는 존재하지 않는 보드 ID로 조회하면 예외를 발생시킨다`() {
        val boardId = 999L

        whenever(boardRepository.findById(boardId))
            .thenReturn(Optional.empty())

        val exception = assertThrows<BoardException> {
            boardReader.getBoardById(boardId)
        }

        assertEquals(BoardErrorCode.BOARD_NOT_FOUND, exception.errorCode)
    }

    @Test
    fun `getBoardById는 존재하는 보드 ID로 조회하면 보드를 반환한다`() {
        val boardId = 1L
        val mockBoard = Board(
            id = boardId,
            title = "테스트 보드",
            logo = "logo.jpg"
        )

        whenever(boardRepository.findById(boardId))
            .thenReturn(Optional.of(mockBoard))

        val result = boardReader.getBoardById(boardId)

        assertSame(mockBoard, result)
    }
}
