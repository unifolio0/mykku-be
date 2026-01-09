package com.example.mykku.board.tool

import com.example.mykku.BaseToolTest
import com.example.mykku.board.domain.Board
import com.example.mykku.board.exception.BoardErrorCode
import com.example.mykku.board.exception.BoardException
import com.example.mykku.board.repository.BoardRepository
import java.util.Optional
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue
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

    @Test
    fun `getAllBoards는 모든 보드 목록을 반환한다`() {
        val boards = listOf(
            Board(id = 1L, title = "게시판1", logo = "logo1.png"),
            Board(id = 2L, title = "게시판2", logo = "logo2.png")
        )

        whenever(boardRepository.findAll()).thenReturn(boards)

        val result = boardReader.getAllBoards()

        assertEquals(2, result.size)
        assertEquals("게시판1", result[0].title)
        assertEquals("게시판2", result[1].title)
    }

    @Test
    fun `getAllBoards는 보드가 없으면 빈 리스트를 반환한다`() {
        whenever(boardRepository.findAll()).thenReturn(emptyList())

        val result = boardReader.getAllBoards()

        assertTrue(result.isEmpty())
    }
}
