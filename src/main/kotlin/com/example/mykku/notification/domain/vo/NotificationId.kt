package com.example.mykku.notification.domain.vo

@JvmInline
value class NotificationId(val value: Long) {
    init {
        require(value > 0) { "NotificationId must be positive" }
    }

    companion object {
        fun of(value: Long): NotificationId = NotificationId(value)
    }
}
