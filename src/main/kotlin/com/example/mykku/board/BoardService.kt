package com.example.mykku.board

import com.example.mykku.board.dto.BoardResponse
import com.example.mykku.board.dto.BoardResponses
import com.example.mykku.board.tool.BoardReader
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BoardService(
    private val boardReader: BoardReader
) {
    @Transactional(readOnly = true)
    fun getBoards(): BoardResponses {
        val boards = boardReader.getAllBoards()
        return BoardResponses(
            boards = boards.map { BoardResponse.from(it) }
        )
    }
}
