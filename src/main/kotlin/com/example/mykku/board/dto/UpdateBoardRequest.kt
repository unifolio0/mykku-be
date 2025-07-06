package com.example.mykku.board.dto

data class UpdateBoardRequest(
    val id: Long,
    val title: String,
    val logo: String
) {

}
