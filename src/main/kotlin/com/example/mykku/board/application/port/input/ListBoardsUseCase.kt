package com.example.mykku.board.application.port.input

import com.example.mykku.board.application.dto.BoardResult

interface ListBoardsUseCase {
    fun listBoards(): List<BoardResult>
}
