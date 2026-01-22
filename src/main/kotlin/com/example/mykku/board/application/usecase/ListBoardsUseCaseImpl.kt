package com.example.mykku.board.application.usecase

import com.example.mykku.board.application.dto.BoardResult
import com.example.mykku.board.application.port.input.ListBoardsUseCase
import com.example.mykku.board.application.port.output.BoardRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ListBoardsUseCaseImpl(
    private val boardRepository: BoardRepository
) : ListBoardsUseCase {

    @Transactional(readOnly = true)
    override fun listBoards(): List<BoardResult> {
        return boardRepository.findAll().map { BoardResult.from(it) }
    }
}
