package com.example.mykku.event.domain.vo

@JvmInline
value class EventParticipationId(val value: Long) {
    companion object {
        fun of(value: Long): EventParticipationId = EventParticipationId(value)
    }
}
