package com.example.mykku.notification.domain.model

@JvmInline
value class NotificationId(val value: Long) {
    init {
        require(value > 0) { "Notification ID must be positive" }
    }
}
