package com.example.mykku.board.tool

import com.example.mykku.board.domain.Board
import com.example.mykku.board.repository.BoardRepository
import com.example.mykku.exception.ErrorCode
import com.example.mykku.exception.MykkuException
import org.springframework.stereotype.Component

@Component
class BoardReader(
    private val boardRepository: BoardRepository
) {
    fun validateDuplicateTitle(title: String) {
        if (boardRepository.existsByTitle(title)) {
            throw MykkuException(ErrorCode.BOARD_DUPLICATE_TITLE)
        }
    }

    fun getBoardById(id: Long): Board {
        return boardRepository.findById(id).orElseThrow {
            MykkuException(ErrorCode.BOARD_NOT_FOUND)
        }
    }
}
