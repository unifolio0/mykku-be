package com.example.mykku.notification.domain.model

@JvmInline
value class FcmTokenId(val value: Long) {
    init {
        require(value > 0) { "FcmToken ID must be positive" }
    }
}
