package com.example.mykku.board

import com.example.mykku.BaseServiceTest
import com.example.mykku.board.domain.Board
import com.example.mykku.board.tool.BoardReader
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BoardServiceTest : BaseServiceTest() {

    @Mock
    private lateinit var boardReader: BoardReader

    @InjectMocks
    private lateinit var boardService: BoardService

    @Test
    fun `getBoards - 게시판 목록을 정상적으로 조회한다`() {
        // given
        val boards = listOf(
            Board(id = 1L, title = "자유게시판", logo = "logo1.png"),
            Board(id = 2L, title = "정보게시판", logo = "logo2.png")
        )

        whenever(boardReader.getAllBoards()).thenReturn(boards)

        // when
        val result = boardService.getBoards()

        // then
        assertEquals(2, result.boards.size)
        assertEquals(1L, result.boards[0].id)
        assertEquals("자유게시판", result.boards[0].title)
        assertEquals("logo1.png", result.boards[0].logo)
        assertEquals(2L, result.boards[1].id)
        assertEquals("정보게시판", result.boards[1].title)
        assertEquals("logo2.png", result.boards[1].logo)
    }

    @Test
    fun `getBoards - 게시판이 없으면 빈 목록을 반환한다`() {
        // given
        whenever(boardReader.getAllBoards()).thenReturn(emptyList())

        // when
        val result = boardService.getBoards()

        // then
        assertTrue(result.boards.isEmpty())
    }
}
