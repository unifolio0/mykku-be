package com.example.mykku.notification.application.dto

import com.example.mykku.notification.domain.vo.NotificationCategory
import org.springframework.data.domain.Pageable

data class GetNotificationsQuery(
    val memberId: String,
    val pageable: Pageable,
    val category: NotificationCategory? = null
)

data class GetUnreadNotificationsQuery(
    val memberId: String,
    val pageable: Pageable,
    val category: NotificationCategory? = null
)

data class GetUnreadCountQuery(
    val memberId: String,
    val category: NotificationCategory? = null
)
