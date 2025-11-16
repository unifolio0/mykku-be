package com.example.mykku.notification.tool

import com.example.mykku.member.domain.Member
import com.example.mykku.notification.domain.NotificationSetting
import com.example.mykku.notification.domain.NotificationType
import com.example.mykku.notification.repository.NotificationSettingRepository
import org.springframework.stereotype.Component

@Component
class NotificationSettingReader(
    private val notificationSettingRepository: NotificationSettingRepository
) {

    fun getSettingsByMember(member: Member): List<NotificationSetting> {
        return notificationSettingRepository.findAllByMember(member)
    }

    fun getSettingByMemberAndType(
        member: Member,
        notificationType: NotificationType
    ): NotificationSetting? {
        return notificationSettingRepository.findByMemberAndNotificationType(
            member,
            notificationType
        ).orElse(null)
    }

    fun isNotificationEnabled(member: Member, notificationType: NotificationType): Boolean {
        val setting = getSettingByMemberAndType(member, notificationType)
        return setting?.isEnabled ?: true
    }

    fun existsByMemberAndType(member: Member, notificationType: NotificationType): Boolean {
        return notificationSettingRepository.existsByMemberAndNotificationType(
            member,
            notificationType
        )
    }
}
