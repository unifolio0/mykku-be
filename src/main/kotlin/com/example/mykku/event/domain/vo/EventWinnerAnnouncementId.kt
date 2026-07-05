package com.example.mykku.event.domain.vo

@JvmInline
value class EventWinnerAnnouncementId(val value: Long) {
    companion object {
        fun of(value: Long): EventWinnerAnnouncementId = EventWinnerAnnouncementId(value)
    }
}
