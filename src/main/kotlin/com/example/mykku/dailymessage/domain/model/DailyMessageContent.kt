package com.example.mykku.dailymessage.domain.model

@JvmInline
value class DailyMessageContent(val value: String) {
    init {
        require(value.length <= MAX_LENGTH) { "DailyMessage content must be $MAX_LENGTH characters or less" }
    }

    companion object {
        const val MAX_LENGTH = 42
    }
}
