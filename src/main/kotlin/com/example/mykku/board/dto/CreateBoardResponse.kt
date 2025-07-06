package com.example.mykku.board.dto

import com.example.mykku.board.domain.Board

data class CreateBoardResponse(
    val id: Long,
    val title: String,
    val logo: String
) {
    constructor(
        board: Board
    ) : this(
        id = board.id!!,
        title = board.title,
        logo = board.logo
    )
}
