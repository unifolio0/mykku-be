package com.example.mykku.notification.application.dto

import com.example.mykku.notification.domain.vo.NotificationDisplayColor
import com.example.mykku.notification.domain.vo.NotificationType

data class CreateNotificationCommand(
    val type: NotificationType,
    val senderId: String?,
    val receiverId: String,
    val content: String,
    val relatedResourceId: Long? = null,
    val relatedResourceType: String? = null,
    val displayColor: NotificationDisplayColor? = null
)

data class MarkAsReadCommand(
    val notificationId: Long,
    val memberId: String
)

data class MarkAllAsReadCommand(
    val memberId: String
)

data class DeleteNotificationCommand(
    val notificationId: Long,
    val memberId: String
)
