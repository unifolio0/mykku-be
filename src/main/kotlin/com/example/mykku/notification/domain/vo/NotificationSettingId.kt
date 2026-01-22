package com.example.mykku.notification.domain.vo

@JvmInline
value class NotificationSettingId(val value: Long) {
    init {
        require(value > 0) { "NotificationSettingId must be positive" }
    }

    companion object {
        fun of(value: Long): NotificationSettingId = NotificationSettingId(value)
    }
}
