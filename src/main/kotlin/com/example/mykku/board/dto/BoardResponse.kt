package com.example.mykku.board.dto

import com.example.mykku.board.domain.Board

data class BoardResponse(
    val id: Long,
    val title: String,
    val logo: String
) {
    companion object {
        fun from(board: Board): BoardResponse {
            return BoardResponse(
                id = board.id!!,
                title = board.title,
                logo = board.logo
            )
        }
    }
}
