package com.example.mykku.notification.application.port.out

import com.example.mykku.member.domain.Member
import com.example.mykku.notification.domain.Notification
import com.example.mykku.notification.domain.NotificationType

interface NotificationRepositoryPort {
    fun createNotification(
        type: NotificationType,
        sender: Member?,
        receiver: Member,
        content: String,
        relatedResourceId: Long? = null,
        relatedResourceType: String? = null
    ): Notification
    fun markAsRead(notification: Notification)
    fun markAllAsRead(notifications: List<Notification>)
    fun markAllAsReadByReceiver(receiver: Member): Int
    fun deleteNotification(notification: Notification)
    fun deleteAllByReceiver(receiver: Member)
}
