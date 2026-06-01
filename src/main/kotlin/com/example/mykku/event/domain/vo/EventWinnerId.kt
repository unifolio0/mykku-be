package com.example.mykku.event.domain.vo

@JvmInline
value class EventWinnerId(val value: Long) {
    companion object {
        fun of(value: Long): EventWinnerId = EventWinnerId(value)
    }
}
