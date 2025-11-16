package com.example.mykku.notification.dto

import com.example.mykku.notification.domain.Notification
import com.example.mykku.notification.domain.NotificationType
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
        fun from(notification: Notification): NotificationResponse {
            return NotificationResponse(
                id = notification.id!!,
                type = notification.type,
                senderNickname = notification.sender?.nickname,
                senderProfileImage = notification.sender?.profileImage,
                content = notification.content,
                isRead = notification.isRead,
                relatedResourceId = notification.relatedResourceId,
                relatedResourceType = notification.relatedResourceType,
                createdAt = notification.createdAt
            )
        }
    }
}
