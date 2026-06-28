package com.example.mykku.board.application.port.input

import com.example.mykku.board.application.dto.BoardResult
import com.example.mykku.board.application.dto.CreateBoardCommand

interface CreateBoardUseCase {
    fun create(command: CreateBoardCommand): BoardResult
}
