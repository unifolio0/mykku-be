package com.example.mykku.board.tool

import com.example.mykku.board.domain.Board
import com.example.mykku.board.repository.BoardRepository
import org.springframework.stereotype.Component

@Component
class BoardWriter(
    private val boardRepository: BoardRepository
) {
    fun createBoard(title: String, logo: String): Board {
        val board = Board(
            title = title,
            logo = logo
        )
        return boardRepository.save(board)
    }

    fun updateBoard(board: Board, title: String, logo: String): Board {
        board.title = title
        board.logo = logo
        return boardRepository.save(board)
    }
}
