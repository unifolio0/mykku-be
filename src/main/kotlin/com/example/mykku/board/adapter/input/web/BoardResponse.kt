package com.example.mykku.board.adapter.input.web

import com.example.mykku.board.application.dto.BoardResult

data class BoardResponse(
    val id: Long,
    val title: String,
    val logo: String
) {
    companion object {
        fun from(result: BoardResult): BoardResponse = BoardResponse(
            id = result.id,
            title = result.title,
            logo = result.logo
        )
    }
}
