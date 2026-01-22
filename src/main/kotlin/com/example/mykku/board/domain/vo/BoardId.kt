package com.example.mykku.board.domain.vo

@JvmInline
value class BoardId(val value: Long) {
    companion object {
        fun of(value: Long): BoardId = BoardId(value)
    }
}
