package com.example.mykku.board.application.port.out

import com.example.mykku.board.domain.model.BoardId

data class BoardSummary(
    val id: Long,
    val title: String,
    val logo: String
)

interface BoardQueryPort {
    fun findSummaryById(id: BoardId): BoardSummary?
    fun existsById(id: BoardId): Boolean
}
