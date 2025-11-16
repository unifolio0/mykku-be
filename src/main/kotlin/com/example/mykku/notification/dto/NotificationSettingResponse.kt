package com.example.mykku.notification.dto

import com.example.mykku.notification.domain.NotificationSetting
import com.example.mykku.notification.domain.NotificationType

data class NotificationSettingResponse(
    val id: Long,
    val notificationType: NotificationType,
    val isEnabled: Boolean
) {
    companion object {
        fun from(setting: NotificationSetting): NotificationSettingResponse {
            return NotificationSettingResponse(
                id = setting.id!!,
                notificationType = setting.notificationType,
                isEnabled = setting.isEnabled
            )
        }
    }
}
