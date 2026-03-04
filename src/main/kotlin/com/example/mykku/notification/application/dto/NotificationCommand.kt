package com.example.mykku.notification.application.dto

import com.example.mykku.notification.domain.vo.NotificationCategory
import com.example.mykku.notification.domain.vo.NotificationDisplayColor
import com.example.mykku.notification.domain.vo.NotificationType

data class CreateNotificationCommand(
    val type: NotificationType,
    val senderId: Long?,
    val receiverId: Long,
    val content: String,
    val relatedResourceId: Long? = null,
    val relatedResourceType: String? = null,
    val displayColor: NotificationDisplayColor? = null
)

data class MarkAsReadCommand(
    val notificationId: Long,
    val memberId: Long
)

data class MarkAllAsReadCommand(
    val memberId: Long,
    val category: NotificationCategory? = null
)

data class DeleteNotificationCommand(
    val notificationId: Long,
    val memberId: Long
)
