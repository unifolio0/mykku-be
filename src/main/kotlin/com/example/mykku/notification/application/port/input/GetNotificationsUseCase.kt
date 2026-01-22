package com.example.mykku.notification.application.port.input

import com.example.mykku.notification.application.dto.GetNotificationsQuery
import com.example.mykku.notification.application.dto.GetUnreadCountQuery
import com.example.mykku.notification.application.dto.GetUnreadNotificationsQuery
import com.example.mykku.notification.application.dto.NotificationResult
import org.springframework.data.domain.Page

interface GetNotificationsUseCase {
    fun getNotifications(query: GetNotificationsQuery): Page<NotificationResult>
    fun getUnreadNotifications(query: GetUnreadNotificationsQuery): Page<NotificationResult>
    fun getUnreadCount(query: GetUnreadCountQuery): Long
}
