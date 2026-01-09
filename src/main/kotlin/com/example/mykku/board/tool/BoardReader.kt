package com.example.mykku.board.tool

import com.example.mykku.board.domain.Board
import com.example.mykku.board.repository.BoardRepository
import com.example.mykku.board.exception.BoardException
import org.springframework.stereotype.Component

@Component
class BoardReader(
    private val boardRepository: BoardRepository
) {
    fun getBoardById(id: Long): Board {
        return boardRepository.findById(id).orElseThrow {
            BoardException.boardNotFound()
        }
    }

    fun getAllBoards(): List<Board> {
        return boardRepository.findAll()
    }
}
