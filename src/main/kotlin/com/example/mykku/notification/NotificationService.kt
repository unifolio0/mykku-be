package com.example.mykku.notification

import com.example.mykku.member.domain.Member
import com.example.mykku.notification.application.port.out.NotificationQueryPort
import com.example.mykku.notification.application.port.out.NotificationRepositoryPort
import com.example.mykku.notification.application.port.out.NotificationSettingQueryPort
import com.example.mykku.notification.domain.NotificationType
import com.example.mykku.notification.dto.NotificationResponse
import com.example.mykku.notification.exception.NotificationException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class NotificationService(
    private val notificationQueryPort: NotificationQueryPort,
    private val notificationRepositoryPort: NotificationRepositoryPort,
    private val notificationSettingQueryPort: NotificationSettingQueryPort,
    private val fcmService: FcmService
) {

    @Transactional(readOnly = true)
    fun getNotifications(member: Member, pageable: Pageable): Page<NotificationResponse> {
        val notifications = notificationQueryPort.getNotificationsByReceiver(member, pageable)
        return notifications.map { NotificationResponse.from(it) }
    }

    @Transactional(readOnly = true)
    fun getUnreadNotifications(member: Member, pageable: Pageable): Page<NotificationResponse> {
        val notifications = notificationQueryPort.getUnreadNotifications(member, pageable)
        return notifications.map { NotificationResponse.from(it) }
    }

    @Transactional(readOnly = true)
    fun getUnreadCount(member: Member): Long {
        return notificationQueryPort.getUnreadCount(member)
    }

    @Transactional
    fun markAsRead(notificationId: Long, member: Member) {
        val notification = notificationQueryPort.getNotificationById(notificationId)

        if (notification.receiver.id != member.id) {
            throw NotificationException.notificationNotAuthorized()
        }

        notificationRepositoryPort.markAsRead(notification)
    }

    @Transactional
    fun markAllAsRead(member: Member) {
        notificationRepositoryPort.markAllAsReadByReceiver(member)
    }

    @Transactional
    fun deleteNotification(notificationId: Long, member: Member) {
        val notification = notificationQueryPort.getNotificationById(notificationId)

        if (notification.receiver.id != member.id) {
            throw NotificationException.notificationNotAuthorized()
        }

        notificationRepositoryPort.deleteNotification(notification)
    }

    @Transactional
    fun createAndSendNotification(
        type: NotificationType,
        sender: Member?,
        receiver: Member,
        content: String,
        relatedResourceId: Long? = null,
        relatedResourceType: String? = null
    ) {
        require(content.length <= 500) {
            "Notification content exceeds maximum length of 500 characters: ${content.length}"
        }

        if (!notificationSettingQueryPort.isNotificationEnabled(receiver, type)) {
            return
        }

        val notification = notificationRepositoryPort.createNotification(
            type = type,
            sender = sender,
            receiver = receiver,
            content = content,
            relatedResourceId = relatedResourceId,
            relatedResourceType = relatedResourceType
        )

        val title = type.description
        fcmService.sendNotificationToMember(
            member = receiver,
            title = title,
            body = content,
            data = mapOf(
                "notificationId" to notification.id.toString(),
                "type" to type.name,
                "relatedResourceId" to (relatedResourceId?.toString() ?: ""),
                "relatedResourceType" to (relatedResourceType ?: "")
            )
        )
    }
}
