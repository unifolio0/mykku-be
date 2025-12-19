package com.example.mykku.fannote.domain.model

@JvmInline
value class FanNoteId(val value: Long) {
    init {
        require(value > 0) { "FanNote ID must be positive" }
    }
}
