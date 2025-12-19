package com.example.mykku.board.application.port.out

import com.example.mykku.board.domain.Board
import com.example.mykku.board.domain.model.BoardDomain
import com.example.mykku.board.domain.model.BoardId

interface BoardRepositoryPort {
    fun save(board: BoardDomain): BoardDomain
    fun findById(id: BoardId): BoardDomain?
    fun findAll(): List<BoardDomain>
    fun existsById(id: BoardId): Boolean

    // Legacy write methods for cross-domain compatibility
    fun createBoard(title: String, logo: String): Board
    fun updateBoard(board: Board, title: String, logo: String): Board
}
