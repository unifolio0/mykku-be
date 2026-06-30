package com.example.mykku.board.application.usecase

import com.example.mykku.board.application.dto.BoardResult
import com.example.mykku.board.application.dto.CreateBoardCommand
import com.example.mykku.board.application.port.input.CreateBoardUseCase
import com.example.mykku.board.application.port.output.BoardRepository
import com.example.mykku.board.domain.entity.Board
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CreateBoardUseCaseImpl(
    private val boardRepository: BoardRepository
) : CreateBoardUseCase {

    @Transactional
    override fun create(command: CreateBoardCommand): BoardResult {
        val board = Board.create(title = command.title, logo = command.logo)
        return BoardResult.from(boardRepository.save(board))
    }
}
