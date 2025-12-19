package com.example.mykku.notification.application.port.out

import com.example.mykku.member.domain.Member
import com.example.mykku.notification.domain.NotificationSetting
import com.example.mykku.notification.domain.NotificationType

interface NotificationSettingRepositoryPort {
    fun createOrUpdateSetting(member: Member, notificationType: NotificationType, isEnabled: Boolean): NotificationSetting
    fun createDefaultSettings(member: Member): List<NotificationSetting>
    fun updateSetting(setting: NotificationSetting, isEnabled: Boolean)
    fun deleteAllByMember(member: Member)
}
