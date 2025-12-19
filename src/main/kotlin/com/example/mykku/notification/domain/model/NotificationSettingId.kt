package com.example.mykku.notification.domain.model

@JvmInline
value class NotificationSettingId(val value: Long) {
    init {
        require(value > 0) { "NotificationSetting ID must be positive" }
    }
}
