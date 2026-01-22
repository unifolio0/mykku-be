package com.example.mykku.scrap.domain.vo

@JvmInline
value class SaveEventId(val value: Long) {
    companion object {
        fun of(value: Long): SaveEventId = SaveEventId(value)
    }
}
