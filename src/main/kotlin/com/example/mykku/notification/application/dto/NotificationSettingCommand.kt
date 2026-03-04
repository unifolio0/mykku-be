package com.example.mykku.notification.application.dto

import com.example.mykku.notification.domain.vo.NotificationType

data class UpdateNotificationSettingCommand(
    val memberId: Long,
    val notificationType: NotificationType,
    val isEnabled: Boolean
)

data class GetNotificationSettingsQuery(
    val memberId: Long
)
