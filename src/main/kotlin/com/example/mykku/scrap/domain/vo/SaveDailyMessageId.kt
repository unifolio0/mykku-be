package com.example.mykku.scrap.domain.vo

@JvmInline
value class SaveDailyMessageId(val value: Long) {
    companion object {
        fun of(value: Long): SaveDailyMessageId = SaveDailyMessageId(value)
    }
}
