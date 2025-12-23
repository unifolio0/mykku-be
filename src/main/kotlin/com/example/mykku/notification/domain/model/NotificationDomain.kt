package com.example.mykku.notification.domain.model

import com.example.mykku.member.domain.model.MemberId
import com.example.mykku.notification.domain.NotificationType
import java.time.Instant

class NotificationDomain private constructor(
    val id: NotificationId?,
    val type: NotificationType,
    val senderId: MemberId?,
    val receiverId: MemberId,
    val content: String,
    private var _isRead: Boolean,
    val relatedResourceId: Long?,
    val relatedResourceType: String?,
    val createdAt: Instant,
    private var _updatedAt: Instant
) {
    val isRead: Boolean get() = _isRead
    val updatedAt: Instant get() = _updatedAt

    companion object {
        fun create(
            type: NotificationType,
            senderId: MemberId?,
            receiverId: MemberId,
            content: String,
            relatedResourceId: Long? = null,
            relatedResourceType: String? = null
        ): NotificationDomain {
            val now = Instant.now()
            return NotificationDomain(
                id = null,
                type = type,
                senderId = senderId,
                receiverId = receiverId,
                content = content,
                _isRead = false,
                relatedResourceId = relatedResourceId,
                relatedResourceType = relatedResourceType,
                createdAt = now,
                _updatedAt = now
            )
        }

        fun reconstitute(
            id: NotificationId,
            type: NotificationType,
            senderId: MemberId?,
            receiverId: MemberId,
            content: String,
            isRead: Boolean,
            relatedResourceId: Long?,
            relatedResourceType: String?,
            createdAt: Instant,
            updatedAt: Instant
        ): NotificationDomain {
            return NotificationDomain(
                id = id,
                type = type,
                senderId = senderId,
                receiverId = receiverId,
                content = content,
                _isRead = isRead,
                relatedResourceId = relatedResourceId,
                relatedResourceType = relatedResourceType,
                createdAt = createdAt,
                _updatedAt = updatedAt
            )
        }
    }

    fun markAsRead() {
        _isRead = true
        _updatedAt = Instant.now()
    }
}
