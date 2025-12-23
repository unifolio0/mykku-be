package com.example.mykku.scrap.domain.model

@JvmInline
value class SaveEventId(val value: Long) {
    init {
        require(value > 0) { "SaveEvent ID must be positive" }
    }
}
