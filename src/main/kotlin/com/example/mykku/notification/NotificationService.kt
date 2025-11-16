package com.example.mykku.notification

import com.example.mykku.member.domain.Member
import com.example.mykku.notification.domain.NotificationType
import com.example.mykku.notification.dto.NotificationResponse
import com.example.mykku.notification.exception.NotificationException
import com.example.mykku.notification.tool.NotificationReader
import com.example.mykku.notification.tool.NotificationSettingReader
import com.example.mykku.notification.tool.NotificationWriter
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class NotificationService(
    private val notificationReader: NotificationReader,
    private val notificationWriter: NotificationWriter,
    private val notificationSettingReader: NotificationSettingReader,
    private val fcmService: FcmService
) {

    @Transactional(readOnly = true)
    fun getNotifications(member: Member, pageable: Pageable): Page<NotificationResponse> {
        val notifications = notificationReader.getNotificationsByReceiver(member, pageable)
        return notifications.map { NotificationResponse.from(it) }
    }

    @Transactional(readOnly = true)
    fun getUnreadNotifications(member: Member, pageable: Pageable): Page<NotificationResponse> {
        val notifications = notificationReader.getUnreadNotifications(member, pageable)
        return notifications.map { NotificationResponse.from(it) }
    }

    @Transactional(readOnly = true)
    fun getUnreadCount(member: Member): Long {
        return notificationReader.getUnreadCount(member)
    }

    @Transactional
    fun markAsRead(notificationId: Long, member: Member) {
        val notification = notificationReader.getNotificationById(notificationId)

        if (notification.receiver.id != member.id) {
            throw NotificationException.notificationNotAuthorized()
        }

        notificationWriter.markAsRead(notification)
    }

    @Transactional
    fun markAllAsRead(member: Member) {
        val notifications = notificationReader.getUnreadNotifications(
            member,
            Pageable.unpaged()
        ).content

        notificationWriter.markAllAsRead(notifications)
    }

    @Transactional
    fun deleteNotification(notificationId: Long, member: Member) {
        val notification = notificationReader.getNotificationById(notificationId)

        if (notification.receiver.id != member.id) {
            throw NotificationException.notificationNotAuthorized()
        }

        notificationWriter.deleteNotification(notification)
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
        if (!notificationSettingReader.isNotificationEnabled(receiver, type)) {
            return
        }

        val notification = notificationWriter.createNotification(
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
