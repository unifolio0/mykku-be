package com.example.mykku.notification.application.port.out

import com.example.mykku.member.domain.Member
import com.example.mykku.notification.domain.NotificationSetting
import com.example.mykku.notification.domain.NotificationType

interface NotificationSettingQueryPort {
    fun getSettingsByMember(member: Member): List<NotificationSetting>
    fun getSettingByMemberAndType(member: Member, notificationType: NotificationType): NotificationSetting?
    fun isNotificationEnabled(member: Member, notificationType: NotificationType): Boolean
    fun existsByMemberAndType(member: Member, notificationType: NotificationType): Boolean
}
