package com.example.mykku.notification.application.dto

import org.springframework.data.domain.Pageable

data class GetNotificationsQuery(
    val memberId: String,
    val pageable: Pageable
)

data class GetUnreadNotificationsQuery(
    val memberId: String,
    val pageable: Pageable
)

data class GetUnreadCountQuery(
    val memberId: String
)
