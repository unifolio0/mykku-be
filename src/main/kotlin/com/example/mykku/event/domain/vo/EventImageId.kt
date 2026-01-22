package com.example.mykku.event.domain.vo

@JvmInline
value class EventImageId(val value: Long) {
    companion object {
        fun of(value: Long): EventImageId = EventImageId(value)
    }
}
