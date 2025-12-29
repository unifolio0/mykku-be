package com.example.mykku.notification.tool

import com.example.mykku.member.domain.Member
import com.example.mykku.notification.domain.Notification
import com.example.mykku.notification.domain.NotificationType
import com.example.mykku.notification.exception.NotificationException
import com.example.mykku.notification.repository.NotificationRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class NotificationReader(
    private val notificationRepository: NotificationRepository
) {

    fun getNotificationById(id: Long): Notification {
        return notificationRepository.findById(id)
            .orElseThrow { NotificationException.notificationNotFound() }
    }

    fun getNotificationsByReceiver(receiver: Member, pageable: Pageable): Page<Notification> {
        return notificationRepository.findAllByReceiverOrderByCreatedAtDesc(receiver, pageable)
    }

    fun getUnreadNotifications(receiver: Member, pageable: Pageable): Page<Notification> {
        return notificationRepository.findAllByReceiverAndIsReadOrderByCreatedAtDesc(
            receiver,
            false,
            pageable
        )
    }

    fun getNotificationsByType(
        receiver: Member,
        type: NotificationType,
        pageable: Pageable
    ): Page<Notification> {
        return notificationRepository.findAllByReceiverAndTypeOrderByCreatedAtDesc(
            receiver,
            type,
            pageable
        )
    }

    fun getUnreadCount(receiver: Member): Long {
        return notificationRepository.countByReceiverAndIsRead(receiver, false)
    }
}
