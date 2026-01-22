package com.example.mykku.notification.application.dto

import com.example.mykku.notification.domain.entity.NotificationSetting
import com.example.mykku.notification.domain.vo.NotificationType

data class NotificationSettingResult(
    val id: Long,
    val notificationType: NotificationType,
    val isEnabled: Boolean
) {
    companion object {
        fun from(setting: NotificationSetting): NotificationSettingResult {
            return NotificationSettingResult(
                id = setting.id!!.value,
                notificationType = setting.notificationType,
                isEnabled = setting.isEnabled
            )
        }
    }
}
