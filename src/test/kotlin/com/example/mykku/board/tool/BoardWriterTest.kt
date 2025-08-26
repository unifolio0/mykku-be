package com.example.mykku.board.tool

import com.example.mykku.board.domain.Board
import com.example.mykku.board.repository.BoardRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertSame

@ExtendWith(MockitoExtension::class)
class BoardWriterTest {

    @Mock
    private lateinit var boardRepository: BoardRepository

    @InjectMocks
    private lateinit var boardWriter: BoardWriter

    @Test
    fun `createBoard는 새로운 보드를 생성하고 저장된 결과를 반환한다`() {
        val title = "테스트 보드"
        val logo = "logo.jpg"
        val mockBoard = Board(
            id = 1L,
            title = title,
            logo = logo
        )

        whenever(boardRepository.save(any<Board>())).thenReturn(mockBoard)

        val result = boardWriter.createBoard(title, logo)

        assertSame(mockBoard, result)
        assertEquals(title, result.title)
        assertEquals(logo, result.logo)
        verify(boardRepository).save(any())
    }

    @Test
    fun `updateBoard는 기존 보드를 업데이트하고 저장된 결과를 반환한다`() {
        val originalBoard = Board(
            id = 1L,
            title = "원래 제목",
            logo = "original.jpg"
        )
        val newTitle = "새로운 제목"
        val newLogo = "new.jpg"
        val updatedBoard = Board(
            id = 1L,
            title = newTitle,
            logo = newLogo
        )

        whenever(boardRepository.save(originalBoard)).thenReturn(updatedBoard)

        val result = boardWriter.updateBoard(originalBoard, newTitle, newLogo)

        assertSame(updatedBoard, result)
        assertEquals(newTitle, originalBoard.title)
        assertEquals(newLogo, originalBoard.logo)
        verify(boardRepository).save(originalBoard)
    }
}