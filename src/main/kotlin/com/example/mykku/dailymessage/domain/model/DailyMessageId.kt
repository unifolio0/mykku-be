package com.example.mykku.dailymessage.domain.model

@JvmInline
value class DailyMessageId(val value: Long) {
    init {
        require(value > 0) { "DailyMessage ID must be positive" }
    }
}
