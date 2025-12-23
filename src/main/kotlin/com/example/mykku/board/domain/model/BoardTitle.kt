package com.example.mykku.board.domain.model

@JvmInline
value class BoardTitle(val value: String) {
    init {
        require(value.isNotBlank()) { "Board title cannot be blank" }
        require(value.length <= MAX_LENGTH) { "Board title must be $MAX_LENGTH characters or less" }
    }

    companion object {
        const val MAX_LENGTH = 16
    }
}
