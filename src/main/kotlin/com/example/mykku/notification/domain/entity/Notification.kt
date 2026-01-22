package com.example.mykku.notification.domain.entity

import com.example.mykku.notification.domain.vo.NotificationId
import com.example.mykku.notification.domain.vo.NotificationType
import java.time.LocalDateTime

class Notification private constructor(
    val id: NotificationId?,
    val type: NotificationType,
    val senderId: String?,
    val receiverId: String,
    val content: String,
    private var _isRead: Boolean,
    val relatedResourceId: Long?,
    val relatedResourceType: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    val isRead: Boolean
        get() = _isRead

    fun markAsRead() {
        _isRead = true
    }

    companion object {
        private const val MAX_CONTENT_LENGTH = 500

        fun create(
            type: NotificationType,
            senderId: String?,
            receiverId: String,
            content: String,
            relatedResourceId: Long? = null,
            relatedResourceType: String? = null
        ): Notification {
            require(content.length <= MAX_CONTENT_LENGTH) {
                "Notification content exceeds maximum length of $MAX_CONTENT_LENGTH characters"
            }

            val now = LocalDateTime.now()
            return Notification(
                id = null,
                type = type,
                senderId = senderId,
                receiverId = receiverId,
                content = content,
                _isRead = false,
                relatedResourceId = relatedResourceId,
                relatedResourceType = relatedResourceType,
                createdAt = now,
                updatedAt = now
            )
        }

        fun reconstitute(
            id: NotificationId,
            type: NotificationType,
            senderId: String?,
            receiverId: String,
            content: String,
            isRead: Boolean,
            relatedResourceId: Long?,
            relatedResourceType: String?,
            createdAt: LocalDateTime,
            updatedAt: LocalDateTime
        ): Notification {
            return Notification(
                id = id,
                type = type,
                senderId = senderId,
                receiverId = receiverId,
                content = content,
                _isRead = isRead,
                relatedResourceId = relatedResourceId,
                relatedResourceType = relatedResourceType,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }
    }
}
