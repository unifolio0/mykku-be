package com.example.mykku.notification.domain.entity

import com.example.mykku.notification.domain.vo.FcmTokenId
import java.time.LocalDateTime

class FcmToken private constructor(
    val id: FcmTokenId?,
    val memberId: String,
    private var _token: String,
    val deviceId: String,
    val deviceType: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    val token: String
        get() = _token

    fun updateToken(newToken: String) {
        _token = newToken
    }

    companion object {
        fun create(
            memberId: String,
            token: String,
            deviceId: String,
            deviceType: String? = null
        ): FcmToken {
            val now = LocalDateTime.now()
            return FcmToken(
                id = null,
                memberId = memberId,
                _token = token,
                deviceId = deviceId,
                deviceType = deviceType,
                createdAt = now,
                updatedAt = now
            )
        }

        fun reconstitute(
            id: FcmTokenId,
            memberId: String,
            token: String,
            deviceId: String,
            deviceType: String?,
            createdAt: LocalDateTime,
            updatedAt: LocalDateTime
        ): FcmToken {
            return FcmToken(
                id = id,
                memberId = memberId,
                _token = token,
                deviceId = deviceId,
                deviceType = deviceType,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }
    }
}
