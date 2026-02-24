package com.example.mykku.notification.application.port.output

import com.example.mykku.notification.domain.entity.Notification
import com.example.mykku.notification.domain.vo.NotificationId
import com.example.mykku.notification.domain.vo.NotificationType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface NotificationRepository {
    fun save(notification: Notification): Notification
    fun findById(id: NotificationId): Notification?
    fun findAllByReceiverId(receiverId: String, pageable: Pageable): Page<Notification>
    fun findAllByReceiverIdAndIsRead(receiverId: String, isRead: Boolean, pageable: Pageable): Page<Notification>
    fun findAllByReceiverIdAndType(receiverId: String, type: NotificationType, pageable: Pageable): Page<Notification>
    fun findAllByReceiverIdAndTypeIn(receiverId: String, types: List<NotificationType>, pageable: Pageable): Page<Notification>
    fun findAllByReceiverIdAndIsReadAndTypeIn(receiverId: String, isRead: Boolean, types: List<NotificationType>, pageable: Pageable): Page<Notification>
    fun countByReceiverIdAndIsRead(receiverId: String, isRead: Boolean): Long
    fun countByReceiverIdAndIsReadAndTypeIn(receiverId: String, isRead: Boolean, types: List<NotificationType>): Long
    fun markAllAsReadByReceiverId(receiverId: String): Int
    fun delete(notification: Notification)
    fun deleteAllByReceiverId(receiverId: String)
}
