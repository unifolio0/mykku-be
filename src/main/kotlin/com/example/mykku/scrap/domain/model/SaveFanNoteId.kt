package com.example.mykku.scrap.domain.model

@JvmInline
value class SaveFanNoteId(val value: Long) {
    init {
        require(value > 0) { "SaveFanNote ID must be positive" }
    }
}
