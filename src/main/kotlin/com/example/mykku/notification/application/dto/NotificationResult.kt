package com.example.mykku.notification.application.dto

import com.example.mykku.notification.domain.entity.Notification
import com.example.mykku.notification.domain.vo.NotificationType
import java.time.LocalDateTime

data class NotificationResult(
    val id: Long,
    val type: NotificationType,
    val senderId: String?,
    val senderNickname: String?,
    val senderProfileImage: String?,
    val content: String,
    val isRead: Boolean,
    val relatedResourceId: Long?,
    val relatedResourceType: String?,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(notification: Notification, senderNickname: String?, senderProfileImage: String?): NotificationResult {
            return NotificationResult(
                id = notification.id!!.value,
                type = notification.type,
                senderId = notification.senderId,
                senderNickname = senderNickname,
                senderProfileImage = senderProfileImage,
                content = notification.content,
                isRead = notification.isRead,
                relatedResourceId = notification.relatedResourceId,
                relatedResourceType = notification.relatedResourceType,
                createdAt = notification.createdAt
            )
        }
    }
}
