package com.example.mykku.notification.application.dto

import com.example.mykku.notification.domain.entity.FcmToken
import java.time.LocalDateTime

data class FcmTokenResult(
    val id: Long,
    val deviceId: String,
    val deviceType: String?,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(fcmToken: FcmToken): FcmTokenResult {
            return FcmTokenResult(
                id = fcmToken.id!!.value,
                deviceId = fcmToken.deviceId,
                deviceType = fcmToken.deviceType,
                createdAt = fcmToken.createdAt
            )
        }
    }
}
