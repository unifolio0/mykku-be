package com.example.mykku.board.tool

import com.example.mykku.board.domain.Board
import com.example.mykku.board.repository.BoardRepository
import com.example.mykku.board.exception.BoardException
import org.springframework.stereotype.Component

@Component
class BoardReader(
    private val boardRepository: BoardRepository
) {
    fun validateDuplicateTitle(title: String) {
        if (boardRepository.existsByTitle(title)) {
            throw BoardException.boardDuplicateTitle()
        }
    }

    fun getBoardById(id: Long): Board {
        return boardRepository.findById(id).orElseThrow {
            BoardException.boardNotFound()
        }
    }
}
