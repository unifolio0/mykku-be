package com.example.mykku.board.application.usecase

import com.example.mykku.board.application.dto.BoardResult
import com.example.mykku.board.application.port.input.GetBoardUseCase
import com.example.mykku.board.application.port.output.BoardRepository
import com.example.mykku.board.domain.vo.BoardId
import com.example.mykku.board.exception.BoardException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetBoardUseCaseImpl(
    private val boardRepository: BoardRepository
) : GetBoardUseCase {

    @Transactional(readOnly = true)
    override fun getBoard(boardId: BoardId): BoardResult {
        val board = boardRepository.findById(boardId)
            ?: throw BoardException.boardNotFound()
        return BoardResult.from(board)
    }
}
