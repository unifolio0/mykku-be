package com.example.mykku.notification.application.port.out

import com.example.mykku.member.domain.Member
import com.example.mykku.notification.domain.Notification
import com.example.mykku.notification.domain.NotificationType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface NotificationQueryPort {
    fun getNotificationById(id: Long): Notification
    fun getNotificationsByReceiver(receiver: Member, pageable: Pageable): Page<Notification>
    fun getUnreadNotifications(receiver: Member, pageable: Pageable): Page<Notification>
    fun getNotificationsByType(receiver: Member, type: NotificationType, pageable: Pageable): Page<Notification>
    fun getUnreadCount(receiver: Member): Long
}
