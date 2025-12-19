package com.example.mykku.notification.infrastructure.adapter

import com.example.mykku.member.domain.Member
import com.example.mykku.notification.application.port.out.NotificationSettingQueryPort
import com.example.mykku.notification.domain.NotificationSetting
import com.example.mykku.notification.domain.NotificationType
import com.example.mykku.notification.repository.NotificationSettingRepository
import org.springframework.stereotype.Component

@Component
class NotificationSettingQueryAdapter(
    private val notificationSettingRepository: NotificationSettingRepository
) : NotificationSettingQueryPort {

    override fun getSettingsByMember(member: Member): List<NotificationSetting> {
        return notificationSettingRepository.findAllByMember(member)
    }

    override fun getSettingByMemberAndType(
        member: Member,
        notificationType: NotificationType
    ): NotificationSetting? {
        return notificationSettingRepository.findByMemberAndNotificationType(
            member,
            notificationType
        ).orElse(null)
    }

    override fun isNotificationEnabled(member: Member, notificationType: NotificationType): Boolean {
        val setting = getSettingByMemberAndType(member, notificationType)
        return setting?.isEnabled ?: true
    }

    override fun existsByMemberAndType(member: Member, notificationType: NotificationType): Boolean {
        return notificationSettingRepository.existsByMemberAndNotificationType(
            member,
            notificationType
        )
    }
}
