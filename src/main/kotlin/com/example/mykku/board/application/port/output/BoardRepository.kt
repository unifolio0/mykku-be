package com.example.mykku.board.application.port.output

import com.example.mykku.board.domain.entity.Board
import com.example.mykku.board.domain.vo.BoardId

interface BoardRepository {
    fun save(board: Board): Board
    fun findById(id: BoardId): Board?
    fun findAll(): List<Board>
    fun delete(board: Board)
    fun existsById(id: BoardId): Boolean
}
