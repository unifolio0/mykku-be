package com.example.mykku.notification.dto

import com.example.mykku.notification.domain.FcmToken
import java.time.LocalDateTime

data class FcmTokenResponse(
    val id: Long,
    val deviceId: String,
    val deviceType: String?,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(fcmToken: FcmToken): FcmTokenResponse {
            return FcmTokenResponse(
                id = fcmToken.id!!,
                deviceId = fcmToken.deviceId,
                deviceType = fcmToken.deviceType,
                createdAt = fcmToken.createdAt
            )
        }
    }
}
