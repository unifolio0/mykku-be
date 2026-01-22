package com.example.mykku.board.application.dto

import com.example.mykku.board.domain.entity.Board

data class BoardResult(
    val id: Long,
    val title: String,
    val logo: String
) {
    companion object {
        fun from(board: Board): BoardResult = BoardResult(
            id = board.id.value,
            title = board.title,
            logo = board.logo
        )
    }
}
