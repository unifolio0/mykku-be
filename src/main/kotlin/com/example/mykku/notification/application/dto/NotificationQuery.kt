package com.example.mykku.notification.application.dto

import com.example.mykku.notification.domain.vo.NotificationCategory
import org.springframework.data.domain.Pageable

data class GetNotificationsQuery(
    val memberId: Long,
    val pageable: Pageable,
    val category: NotificationCategory? = null
)

data class GetUnreadNotificationsQuery(
    val memberId: Long,
    val pageable: Pageable,
    val category: NotificationCategory? = null
)

data class GetUnreadCountQuery(
    val memberId: Long,
    val category: NotificationCategory? = null
)
