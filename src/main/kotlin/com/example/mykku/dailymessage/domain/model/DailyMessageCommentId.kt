package com.example.mykku.dailymessage.domain.model

@JvmInline
value class DailyMessageCommentId(val value: Long) {
    init {
        require(value > 0) { "DailyMessage Comment ID must be positive" }
    }
}
