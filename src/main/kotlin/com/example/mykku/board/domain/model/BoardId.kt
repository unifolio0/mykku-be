package com.example.mykku.board.domain.model

@JvmInline
value class BoardId(val value: Long) {
    init {
        require(value > 0) { "Board ID must be positive" }
    }
}
