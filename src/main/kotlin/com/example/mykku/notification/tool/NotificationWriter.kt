package com.example.mykku.notification.tool

import com.example.mykku.member.domain.Member
import com.example.mykku.notification.domain.Notification
import com.example.mykku.notification.domain.NotificationType
import com.example.mykku.notification.repository.NotificationRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class NotificationWriter(
    private val notificationRepository: NotificationRepository
) {

    @Transactional
    fun createNotification(
        type: NotificationType,
        sender: Member?,
        receiver: Member,
        content: String,
        relatedResourceId: Long? = null,
        relatedResourceType: String? = null
    ): Notification {
        val notification = Notification.create(
            type = type,
            sender = sender,
            receiver = receiver,
            content = content,
            relatedResourceId = relatedResourceId,
            relatedResourceType = relatedResourceType
        )
        return notificationRepository.save(notification)
    }

    @Transactional
    fun markAsRead(notification: Notification) {
        notification.markAsRead()
    }

    @Transactional
    fun markAllAsRead(notifications: List<Notification>) {
        notifications.forEach { it.markAsRead() }
    }

    @Transactional
    fun markAllAsReadByReceiver(receiver: Member): Int {
        return notificationRepository.markAllAsReadByReceiver(receiver)
    }

    @Transactional
    fun deleteNotification(notification: Notification) {
        notificationRepository.delete(notification)
    }

    @Transactional
    fun deleteAllByReceiver(receiver: Member) {
        notificationRepository.deleteAllByReceiver(receiver)
    }
}
