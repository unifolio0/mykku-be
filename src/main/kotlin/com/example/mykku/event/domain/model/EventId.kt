package com.example.mykku.event.domain.model

@JvmInline
value class EventId(val value: Long) {
    init {
        require(value > 0) { "Event ID must be positive" }
    }
}
