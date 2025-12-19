package com.example.mykku.notification.infrastructure.adapter

import com.example.mykku.member.domain.Member
import com.example.mykku.notification.application.port.out.NotificationRepositoryPort
import com.example.mykku.notification.domain.Notification
import com.example.mykku.notification.domain.NotificationType
import com.example.mykku.notification.repository.NotificationRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class NotificationRepositoryAdapter(
    private val notificationRepository: NotificationRepository
) : NotificationRepositoryPort {

    @Transactional
    override fun createNotification(
        type: NotificationType,
        sender: Member?,
        receiver: Member,
        content: String,
        relatedResourceId: Long?,
        relatedResourceType: String?
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
    override fun markAsRead(notification: Notification) {
        notification.markAsRead()
    }

    @Transactional
    override fun markAllAsRead(notifications: List<Notification>) {
        notifications.forEach { it.markAsRead() }
    }

    @Transactional
    override fun markAllAsReadByReceiver(receiver: Member): Int {
        return notificationRepository.markAllAsReadByReceiver(receiver)
    }

    @Transactional
    override fun deleteNotification(notification: Notification) {
        notificationRepository.delete(notification)
    }

    @Transactional
    override fun deleteAllByReceiver(receiver: Member) {
        notificationRepository.deleteAllByReceiver(receiver)
    }
}
