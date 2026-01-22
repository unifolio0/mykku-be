package com.example.mykku.board.application.port.input

import com.example.mykku.board.application.dto.BoardResult
import com.example.mykku.board.domain.vo.BoardId

interface GetBoardUseCase {
    fun getBoard(boardId: BoardId): BoardResult
}
