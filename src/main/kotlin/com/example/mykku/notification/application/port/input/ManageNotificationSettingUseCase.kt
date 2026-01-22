package com.example.mykku.notification.application.port.input

import com.example.mykku.notification.application.dto.GetNotificationSettingsQuery
import com.example.mykku.notification.application.dto.NotificationSettingResult
import com.example.mykku.notification.application.dto.UpdateNotificationSettingCommand

interface ManageNotificationSettingUseCase {
    fun getOrCreateSettings(query: GetNotificationSettingsQuery): List<NotificationSettingResult>
    fun updateSetting(command: UpdateNotificationSettingCommand): NotificationSettingResult
}
