package com.example.mykku.dailymessage.domain.vo

@JvmInline
value class DailyMessageId(val value: Long) {
    companion object {
        fun of(value: Long): DailyMessageId = DailyMessageId(value)
    }
}
