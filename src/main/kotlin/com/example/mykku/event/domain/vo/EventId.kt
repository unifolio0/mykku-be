package com.example.mykku.event.domain.vo

@JvmInline
value class EventId(val value: Long) {
    companion object {
        fun of(value: Long): EventId = EventId(value)
    }
}
