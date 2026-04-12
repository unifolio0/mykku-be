package com.example.mykku.notification.adapter.input.web

import com.example.mykku.notification.application.dto.FcmTokenResult
import com.example.mykku.notification.application.dto.NotificationResult
import com.example.mykku.notification.application.dto.NotificationSettingResult
import com.example.mykku.notification.domain.vo.NotificationType
import java.time.LocalDateTime

data class NotificationResponse(
    val id: Long,
    val type: NotificationType,
    val senderNickname: String?,
    val senderProfileImage: String?,
    val content: String,
    val isRead: Boolean,
    val relatedResourceId: Long?,
    val relatedResourceType: String?,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(result: NotificationResult): NotificationResponse {
            return NotificationResponse(
                id = result.id,
                type = result.type,
                senderNickname = result.senderNickname,
                senderProfileImage = result.senderProfileImage,
                content = result.content,
                isRead = result.isRead,
                relatedResourceId = result.relatedResourceId,
                relatedResourceType = result.relatedResourceType,
                createdAt = result.createdAt
            )
        }
    }
}

data class FcmTokenResponse(
    val id: Long,
    val deviceId: String,
    val deviceType: String?,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(result: FcmTokenResult): FcmTokenResponse {
            return FcmTokenResponse(
                id = result.id,
                deviceId = result.deviceId,
                deviceType = result.deviceType,
                createdAt = result.createdAt
            )
        }
    }
}

data class NotificationSettingResponse(
    val id: Long,
    val notificationType: NotificationType,
    val isEnabled: Boolean
) {
    companion object {
        fun from(result: NotificationSettingResult): NotificationSettingResponse {
            return NotificationSettingResponse(
                id = result.id,
                notificationType = result.notificationType,
                isEnabled = result.isEnabled
            )
        }
    }
}
