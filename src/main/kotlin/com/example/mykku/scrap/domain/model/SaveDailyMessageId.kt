package com.example.mykku.scrap.domain.model

@JvmInline
value class SaveDailyMessageId(val value: Long) {
    init {
        require(value > 0) { "SaveDailyMessage ID must be positive" }
    }
}
