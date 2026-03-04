package com.example.mykku.notification.application.port.output

import com.example.mykku.notification.domain.entity.Notification
import com.example.mykku.notification.domain.vo.NotificationId
import com.example.mykku.notification.domain.vo.NotificationType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface NotificationRepository {
    fun save(notification: Notification): Notification
    fun findById(id: NotificationId): Notification?
    fun findAllByReceiverId(receiverId: Long, pageable: Pageable): Page<Notification>
    fun findAllByReceiverIdAndIsRead(receiverId: Long, isRead: Boolean, pageable: Pageable): Page<Notification>
    fun findAllByReceiverIdAndType(receiverId: Long, type: NotificationType, pageable: Pageable): Page<Notification>
    fun findAllByReceiverIdAndTypeIn(receiverId: Long, types: List<NotificationType>, pageable: Pageable): Page<Notification>
    fun findAllByReceiverIdAndIsReadAndTypeIn(receiverId: Long, isRead: Boolean, types: List<NotificationType>, pageable: Pageable): Page<Notification>
    fun countByReceiverIdAndIsRead(receiverId: Long, isRead: Boolean): Long
    fun countByReceiverIdAndIsReadAndTypeIn(receiverId: Long, isRead: Boolean, types: List<NotificationType>): Long
    fun markAllAsReadByReceiverId(receiverId: Long): Int
    fun markAllAsReadByReceiverIdAndTypeIn(receiverId: Long, types: List<NotificationType>): Int
    fun delete(notification: Notification)
    fun deleteAllByReceiverId(receiverId: Long)
}
