package com.example.mykku.notification.domain.vo

@JvmInline
value class FcmTokenId(val value: Long) {
    init {
        require(value > 0) { "FcmTokenId must be positive" }
    }

    companion object {
        fun of(value: Long): FcmTokenId = FcmTokenId(value)
    }
}
